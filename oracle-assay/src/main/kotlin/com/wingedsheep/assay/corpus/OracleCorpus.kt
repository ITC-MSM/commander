package com.wingedsheep.assay.corpus

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.zip.GZIPInputStream

/**
 * The corpus the touchstone runs over: every unique Oracle text Scryfall knows.
 *
 * Source is Scryfall's `oracle_cards` bulk file — one card object per Oracle ID — which Scryfall
 * now serves as gzipped **JSONL**. That matters: it streams a line at a time, so ~38k cards cost a
 * bounded amount of heap instead of a 150 MB `JsonElement` tree.
 *
 * The download is cached next to the per-set caches that `:mtgish-tooling` and `scripts/card-status`
 * already share (`~/.cache/scryfall/`), under a distinct `_bulk-` prefix so it cannot collide with
 * a set code. Assay does not read or write *their* files — it needs the whole corpus, not a set at a
 * time, and sharing a *directory* is all the coupling that is wanted. [SetMembership] keeps its own
 * per-set lists there under a `_setlist-` prefix, for the one question this file cannot answer:
 * `setCode` is a card's representative printing, not the sets it was printed in.
 */
object OracleCorpus {

    private const val BULK_INDEX = "https://api.scryfall.com/bulk-data"

    /** How long a downloaded bulk file is considered current. Scryfall rebuilds it daily. */
    private val FRESH_FOR: Duration = Duration.ofDays(7)

    private val CACHE_ROOT = File(System.getProperty("user.home"), ".cache/scryfall")
    private val BULK_FILE = File(CACHE_ROOT, "_bulk-oracle-cards.jsonl.gz")

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Every assayable card, streamed. The sequence reads the gzip lazily, so `--limit` genuinely
     * stops early instead of parsing 38k cards first.
     *
     * @param refresh force a re-download even if the cached bulk file is current.
     */
    fun cards(refresh: Boolean = false): Sequence<OracleCard> {
        val file = ensureBulk(refresh)
        return sequence {
            BufferedReader(InputStreamReader(GZIPInputStream(file.inputStream()), StandardCharsets.UTF_8))
                .use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break
                        val trimmed = line.trim().trimEnd(',')
                        if (trimmed.isEmpty() || trimmed == "[" || trimmed == "]") continue
                        val card = ScryfallJson.read(trimmed)
                        if (card != null) yield(card)
                    }
                }
        }
    }

    /** True when a usable bulk file is already on disk, so the CLI can say so before downloading. */
    fun isCached(): Boolean = BULK_FILE.isFile && BULK_FILE.length() > 0

    fun cacheFile(): File = BULK_FILE

    // -----------------------------------------------------------------------------------------
    // Download
    // -----------------------------------------------------------------------------------------

    private fun JsonObject.str(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content

    private fun ensureBulk(refresh: Boolean): File {
        if (!refresh && isCached() && isFresh()) return BULK_FILE
        val uri = runCatching { bulkDownloadUri() }.getOrElse { e ->
            if (isCached()) {
                System.err.println("warning: could not reach Scryfall ($e); using the cached bulk file")
                return BULK_FILE
            }
            throw IOException("no cached Oracle bulk file and Scryfall is unreachable: $e", e)
        }
        download(uri, BULK_FILE)
        return BULK_FILE
    }

    private fun isFresh(): Boolean =
        Instant.ofEpochMilli(BULK_FILE.lastModified()).isAfter(Instant.now().minus(FRESH_FOR))

    private fun bulkDownloadUri(): String {
        val payload = json.parseToJsonElement(ScryfallHttp.get(BULK_INDEX)).jsonObject
        val entries = (payload["data"] as? JsonArray)?.filterIsInstance<JsonObject>().orEmpty()
        val oracle = entries.firstOrNull { it.str("type") == "oracle_cards" }
            ?: error("Scryfall bulk-data index has no oracle_cards entry")
        return oracle.str("jsonl_download_uri")
            ?: oracle.str("download_uri")
            ?: error("oracle_cards entry has no download URI")
    }

    /**
     * Downloads to a sibling temp file and renames on success, so an interrupted fetch can never
     * leave a truncated gzip behind that every later run would then fail to read.
     */
    private fun download(url: String, target: File) {
        System.err.println("assay: downloading the Oracle bulk from Scryfall (~24 MB) …")
        target.parentFile.mkdirs()
        val tmp = File(target.parentFile, "${target.name}.part")
        val conn = ScryfallHttp.open(url)
        try {
            if (conn.responseCode >= 400) throw ScryfallHttpException(conn.responseCode, url)
            conn.inputStream.use { input -> tmp.outputStream().use { output -> input.copyTo(output) } }
        } finally {
            conn.disconnect()
        }
        if (target.exists()) target.delete()
        check(tmp.renameTo(target)) { "could not move $tmp into place at $target" }
        System.err.println("assay: cached at $target")
    }
}
