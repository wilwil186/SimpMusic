package com.maxrave.simpmusic.expect

/**
 * A Google/YouTube session lifted directly from an installed desktop browser.
 *
 * @param browserName human-readable source browser (e.g. "Brave", "Firefox").
 * @param cookieHeader `name=value; name=value` header string for the YTMusic API.
 * @param netscapeCookie Netscape cookie-file text for the yt-dlp cookie file.
 */
data class BrowserCookieImport(
    val browserName: String,
    val cookieHeader: String,
    val netscapeCookie: String,
)

/**
 * Reads the user's existing Google/YouTube session cookies directly from an
 * installed desktop browser (Firefox or any Chromium-based browser) so the user
 * only has to log in once in their normal browser — no extension, no manual
 * cookie pasting.
 *
 * Returns the first browser that has a logged-in YouTube session, or `null`
 * when none is found. Only meaningful on Desktop; Android uses its embedded
 * WebView login instead and returns `null` here.
 */
expect suspend fun importGoogleCookiesFromBrowser(): BrowserCookieImport?
