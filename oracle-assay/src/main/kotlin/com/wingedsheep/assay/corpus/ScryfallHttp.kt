package com.wingedsheep.assay.corpus

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

/**
 * The one place Assay talks to Scryfall.
 *
 * Extracted when [SetMembership] became the second caller. Two copies of a connection setup is the
 * kind of duplication that drifts silently — Scryfall's policy requires a `User-Agent`, and a second
 * copy that grows a longer timeout while the first does not produces a hang nobody attributes to the
 * missing edit.
 *
 * No dependency arrives with it: this is `java.net`, so `:mtg-sdk` remains the module's only
 * production dependency.
 */
internal object ScryfallHttp {

    private const val USER_AGENT = "argentum-assay/1.0"
    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 120_000

    /** A configured connection, for callers that stream the body rather than reading it whole. */
    fun open(url: String): HttpURLConnection =
        (URI(url).toURL().openConnection() as HttpURLConnection).apply {
            setRequestProperty("User-Agent", USER_AGENT)
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
        }

    fun get(url: String): String {
        val conn = open(url)
        conn.setRequestProperty("Accept", "application/json")
        try {
            if (conn.responseCode >= 400) throw ScryfallHttpException(conn.responseCode, url)
            return conn.inputStream.readBytes().toString(StandardCharsets.UTF_8)
        } finally {
            conn.disconnect()
        }
    }
}

/**
 * Carries the status code, because callers have to tell two failures apart.
 *
 * Scryfall answers a search that matched nothing with **404**, so for a `set:` query that status is
 * the answer "no such set" — a fact worth caching — while any other failure means "ask again later"
 * and must fall back to whatever is already on disk. A plain [IOException] collapses the two.
 */
internal class ScryfallHttpException(val status: Int, url: String) : IOException("$url -> HTTP $status")
