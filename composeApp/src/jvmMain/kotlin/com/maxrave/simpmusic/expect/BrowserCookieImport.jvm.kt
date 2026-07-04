package com.maxrave.simpmusic.expect

import com.maxrave.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import de.swiesend.secretservice.simple.SimpleCollection
import java.io.File
import java.nio.file.Files
import java.sql.DriverManager
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val TAG = "BrowserCookieImport"

// A single cookie lifted from a browser store, in a browser-agnostic shape.
private data class RawCookie(
    val host: String,
    val name: String,
    val value: String,
    val path: String,
    val secure: Boolean,
    // Unix epoch seconds; 0 == session cookie.
    val expiry: Long,
)

/**
 * Reads the user's YouTube session from an installed desktop browser so login
 * is a single "Import session from browser" click — no extension, no pasting.
 *
 * Tries each Chromium-based browser (Brave, Chrome, Chromium, Edge, Vivaldi)
 * and Firefox, returning the first one that holds a real logged-in YouTube
 * session. Chromium cookie values are encrypted; the AES key is pulled from the
 * OS keyring via the Secret Service. Firefox stores cookies in the clear.
 */
actual suspend fun importGoogleCookiesFromBrowser(): BrowserCookieImport? =
    withContext(Dispatchers.IO) {
        val home = System.getProperty("user.home") ?: return@withContext null

        // Chromium family first — that is where most users are logged in — then Firefox.
        val chromium =
            listOf(
                ChromiumBrowser("Brave", "$home/.config/BraveSoftware/Brave-Browser", listOf("brave")),
                ChromiumBrowser("Google Chrome", "$home/.config/google-chrome", listOf("chrome", "chromium")),
                ChromiumBrowser("Chromium", "$home/.config/chromium", listOf("chromium", "chrome")),
                ChromiumBrowser("Microsoft Edge", "$home/.config/microsoft-edge", listOf("chromium", "chrome")),
                ChromiumBrowser("Vivaldi", "$home/.config/vivaldi", listOf("vivaldi", "chrome")),
            )

        for (browser in chromium) {
            runCatching { readChromiumSession(browser) }
                .getOrElse {
                    Logger.w(TAG, "Chromium read failed for ${browser.displayName}: ${it.message}")
                    null
                }?.let { return@withContext it }
        }

        runCatching { readFirefoxSession(home) }
            .getOrElse {
                Logger.w(TAG, "Firefox read failed: ${it.message}")
                null
            }?.let { return@withContext it }

        Logger.w(TAG, "No logged-in YouTube session found in any browser")
        null
    }

private data class ChromiumBrowser(
    val displayName: String,
    val userDataDir: String,
    // Candidate `application` attribute values under which the browser stores
    // its "Safe Storage" key in the Secret Service.
    val keyringApps: List<String>,
)

// ---------------------------------------------------------------------------
// Chromium (Brave / Chrome / Chromium / Edge / Vivaldi)
// ---------------------------------------------------------------------------

private fun readChromiumSession(browser: ChromiumBrowser): BrowserCookieImport? {
    val userData = File(browser.userDataDir)
    if (!userData.isDirectory) return null

    // Default profile first, then "Profile N" / any dir holding a Cookies DB.
    val profiles =
        buildList {
            File(userData, "Default").takeIf { it.isDirectory }?.let { add(it) }
            userData
                .listFiles { f -> f.isDirectory && (f.name.startsWith("Profile") || f.name == "Guest Profile") }
                ?.sortedBy { it.name }
                ?.let { addAll(it) }
        }.distinct()

    if (profiles.isEmpty()) return null

    val aesKey by lazy { chromiumAesKey(browser.keyringApps) }

    for (profile in profiles) {
        val cookiesDb = File(profile, "Cookies")
        if (!cookiesDb.isFile) continue

        val cookies =
            runCatching { readChromiumCookies(cookiesDb) { encrypted -> decryptChromiumValue(encrypted, aesKey) } }
                .getOrElse {
                    Logger.w(TAG, "Failed reading ${browser.displayName}/${profile.name} cookies: ${it.message}")
                    emptyList()
                }

        buildImport(browser.displayName, cookies)?.let { return it }
    }
    return null
}

/** Pull "<Browser> Safe Storage" password from the keyring and derive the AES key. */
// SimpleCollection is the stable, synchronous entry point; its successor
// "functional" API in secret-service 3.x is still alpha, so we keep this one.
@Suppress("DEPRECATION")
private fun chromiumAesKey(keyringApps: List<String>): ByteArray {
    val password: CharArray =
        runCatching {
            if (!SimpleCollection.isAvailable()) return@runCatching null
            SimpleCollection().use { collection ->
                for (app in keyringApps) {
                    val items = collection.getItems(mapOf("application" to app)) ?: continue
                    for (path in items) {
                        collection.getSecret(path)?.takeIf { it.isNotEmpty() }?.let { return@use it }
                    }
                }
                null
            }
        }.getOrNull()
            // Chromium's fallback when no keyring is available: password "peanuts" (v10).
            ?: "peanuts".toCharArray()

    return pbkdf2(password, "saltysalt".toByteArray(Charsets.UTF_8), iterations = 1, keyBits = 128)
}

private fun readChromiumCookies(
    cookiesDb: File,
    decrypt: (ByteArray) -> String?,
): List<RawCookie> =
    withCopiedSqlite(cookiesDb) { conn ->
        val metaVersion =
            conn.createStatement().use { st ->
                runCatching {
                    st.executeQuery("SELECT value FROM meta WHERE key='version'").use { rs ->
                        if (rs.next()) rs.getString(1).toIntOrNull() ?: 0 else 0
                    }
                }.getOrDefault(0)
            }

        val out = mutableListOf<RawCookie>()
        conn
            .prepareStatement(
                "SELECT host_key, name, value, encrypted_value, path, is_secure, expires_utc " +
                    "FROM cookies WHERE host_key LIKE '%youtube.com'",
            ).use { ps ->
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val plain = rs.getString("value") ?: ""
                        val value =
                            if (plain.isNotEmpty()) {
                                plain
                            } else {
                                val enc = rs.getBytes("encrypted_value") ?: continue
                                decryptChromiumBytes(enc, decrypt, metaVersion) ?: continue
                            }
                        out += RawCookie(
                            host = rs.getString("host_key") ?: "",
                            name = rs.getString("name") ?: "",
                            value = value,
                            path = rs.getString("path") ?: "/",
                            secure = rs.getInt("is_secure") != 0,
                            expiry = chromeTimeToUnix(rs.getLong("expires_utc")),
                        )
                    }
                }
            }
        out
    }

/** Applies the meta-version-aware 32-byte domain-hash stripping around the raw AES decrypt. */
private fun decryptChromiumBytes(
    encrypted: ByteArray,
    decrypt: (ByteArray) -> String?,
    metaVersion: Int,
): String? {
    // decrypt() returns the already-depadded plaintext bytes as a Latin-1 string.
    val depadded = decrypt(encrypted)?.toByteArray(Charsets.ISO_8859_1) ?: return null
    // Chromium >= v24 prefixes the value with a 32-byte SHA-256 hash of the domain.
    val body = if (metaVersion >= 24 && depadded.size >= 32) depadded.copyOfRange(32, depadded.size) else depadded
    return String(body, Charsets.UTF_8)
}

/** AES-128-CBC decrypt of a Chromium `v10`/`v11` encrypted_value; returns depadded bytes as Latin-1. */
private fun decryptChromiumValue(
    encrypted: ByteArray,
    aesKey: ByteArray,
): String? {
    if (encrypted.size <= 3) return null
    // Strip the 3-byte version tag ("v10"/"v11").
    val body = encrypted.copyOfRange(3, encrypted.size)
    val iv = ByteArray(16) { ' '.code.toByte() }
    val cipher = Cipher.getInstance("AES/CBC/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aesKey, "AES"), IvParameterSpec(iv))
    var plain = runCatching { cipher.doFinal(body) }.getOrNull() ?: return null
    // Remove PKCS#7 padding.
    val pad = plain.lastOrNull()?.toInt()?.and(0xFF) ?: 0
    if (pad in 1..16 && pad <= plain.size) plain = plain.copyOf(plain.size - pad)
    return String(plain, Charsets.ISO_8859_1)
}

// ---------------------------------------------------------------------------
// Firefox
// ---------------------------------------------------------------------------

private fun readFirefoxSession(home: String): BrowserCookieImport? {
    val profileRoots =
        listOf(
            "$home/.mozilla/firefox",
            "$home/snap/firefox/common/.mozilla/firefox",
            "$home/.var/app/org.mozilla.firefox/.mozilla/firefox",
        ).map(::File).filter { it.isDirectory }

    val cookieDbs =
        profileRoots
            .flatMap { root -> root.listFiles { f -> f.isDirectory }?.toList() ?: emptyList() }
            .map { File(it, "cookies.sqlite") }
            .filter { it.isFile }

    for (db in cookieDbs) {
        val cookies =
            runCatching { readFirefoxCookies(db) }
                .getOrElse {
                    Logger.w(TAG, "Failed reading Firefox ${db.parentFile.name}: ${it.message}")
                    emptyList()
                }
        buildImport("Firefox", cookies)?.let { return it }
    }
    return null
}

private fun readFirefoxCookies(db: File): List<RawCookie> =
    withCopiedSqlite(db) { conn ->
        val out = mutableListOf<RawCookie>()
        conn
            .prepareStatement(
                "SELECT host, name, value, path, isSecure, expiry FROM moz_cookies WHERE host LIKE '%youtube.com'",
            ).use { ps ->
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        out += RawCookie(
                            host = rs.getString("host") ?: "",
                            name = rs.getString("name") ?: "",
                            value = rs.getString("value") ?: "",
                            path = rs.getString("path") ?: "/",
                            secure = rs.getInt("isSecure") != 0,
                            expiry = rs.getLong("expiry"),
                        )
                    }
                }
            }
        out
    }

// ---------------------------------------------------------------------------
// Shared helpers
// ---------------------------------------------------------------------------

/**
 * Builds the final import payload from raw cookies, or null if the set does not
 * look like a real logged-in YouTube session.
 */
private fun buildImport(
    browserName: String,
    cookies: List<RawCookie>,
): BrowserCookieImport? {
    if (cookies.isEmpty()) return null
    // De-duplicate by name, keeping the last (typically the most specific host).
    val byName = cookies.filter { it.name.isNotEmpty() && it.value.isNotEmpty() }.associateBy { it.name }
    // A logged-in session needs the SAPISID (for SAPISIDHASH auth) and a SID cookie.
    val hasAuth = byName.containsKey("SAPISID") && (byName.containsKey("__Secure-3PSID") || byName.containsKey("SID"))
    if (!hasAuth) return null

    val header = byName.values.joinToString("; ") { "${it.name}=${it.value}" }
    val netscape = toNetscape(cookies.filter { it.name.isNotEmpty() && it.value.isNotEmpty() })
    Logger.w(TAG, "Imported ${byName.size} YouTube cookies from $browserName")
    return BrowserCookieImport(browserName, header, netscape)
}

private fun toNetscape(cookies: List<RawCookie>): String =
    buildString {
        append("# Netscape HTTP Cookie File\n")
        append("# Imported from browser by SimpMusic\n")
        append("# This is a generated file! Do not edit.\n\n")
        for (c in cookies) {
            val includeSub = if (c.host.startsWith(".")) "TRUE" else "FALSE"
            val secure = if (c.secure) "TRUE" else "FALSE"
            append(
                listOf(c.host, includeSub, c.path, secure, c.expiry.toString(), c.name, c.value)
                    .joinToString("\t"),
            )
            append("\n")
        }
    }

/** Chromium `expires_utc` (microseconds since 1601-01-01) → Unix epoch seconds. */
private fun chromeTimeToUnix(expiresUtc: Long): Long {
    if (expiresUtc <= 0L) return 0L
    return expiresUtc / 1_000_000L - 11_644_473_600L
}

private fun pbkdf2(
    password: CharArray,
    salt: ByteArray,
    iterations: Int,
    keyBits: Int,
): ByteArray {
    val spec = PBEKeySpec(password, salt, iterations, keyBits)
    return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
}

/**
 * Copies a (possibly WAL-backed, in-use) SQLite DB and its sidecar files to a
 * temp location, opens the copy read-only, runs [block], then cleans up. Copying
 * avoids "database is locked" while the browser is running.
 */
private fun <T> withCopiedSqlite(
    db: File,
    block: (java.sql.Connection) -> T,
): T {
    runCatching { Class.forName("org.sqlite.JDBC") }
    val tmpDir = Files.createTempDirectory("simpmusic-cookies").toFile()
    try {
        val dest = File(tmpDir, db.name)
        db.copyTo(dest, overwrite = true)
        for (suffix in listOf("-wal", "-shm")) {
            val side = File(db.parentFile, db.name + suffix)
            if (side.isFile) side.copyTo(File(tmpDir, db.name + suffix), overwrite = true)
        }
        DriverManager.getConnection("jdbc:sqlite:${dest.absolutePath}").use { conn ->
            return block(conn)
        }
    } finally {
        tmpDir.deleteRecursively()
    }
}
