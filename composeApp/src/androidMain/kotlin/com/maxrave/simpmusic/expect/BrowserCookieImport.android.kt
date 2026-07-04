package com.maxrave.simpmusic.expect

// Android logs in through the embedded WebView, which captures the cookie
// automatically — there is no separate "import from browser" step here.
actual suspend fun importGoogleCookiesFromBrowser(): BrowserCookieImport? = null
