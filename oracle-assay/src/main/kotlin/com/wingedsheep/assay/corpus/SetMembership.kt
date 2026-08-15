package com.wingedsheep.assay.corpus

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Which cards were printed in one set — the thing `--set` has to consult to mean what it says.
 *
 * ## Why [OracleCard.setCode] is not that
 *
 * The corpus is Scryfall's `oracle_cards` bulk: one object per Oracle ID, with a single
 * *representative* printing standing in for every printing a card has. Filtering that field by set
 * code therefore answers "which cards does Scryfall happen to show under this set", not "which cards
 * are in this set", and for anything old or heavily reprinted the two differ by most of the set.
 *
 * Portal is the worked example that produced this file. It has 200 cards; exactly 53 of them carry
 * `por`. Blaze is shown as `bbd`, Raise Dead as `w17`, Wild Griffin as `cn2` — a Portal original that
 * was later reprinted is credited to the newest printing — and Portal's own reprints of older cards
 * are credited to the older set, so they are missing in the other direction too. A per-set fineness
 * number computed over the survivors is measuring an arbitrary quarter of the set.
 *
 * ## Why per set rather than one global index
 *
 * The obvious fix is an Oracle-ID → set-codes map built from the `default_cards` bulk, and it is the
 * wrong shape here: 77 MB downloaded and half a million objects scanned, by every run of the
 * explorer, to serve a filter that is always **one set at a time**. Scryfall's search API answers
 * exactly the question that is being asked in ~200 KB, so membership is fetched per set, cached on
 * disk, and paid for only by a run that actually names a set.
 *
 * The cache lives beside the Oracle bulk in `~/.cache/scryfall/`, under a `_setlist-` prefix that
 * cannot collide with the per-set files `scripts/card-status` and `:mtgish-tooling` keep there —
 * sharing the *directory* is still all the coupling that is wanted.
 *
 * ## Fail-closed, and never throwing
 *
 * [of] returns `null` for a set that cannot be resolved, and callers treat that as "match nothing"
 * plus a message rather than as "match everything". A filter that silently degrades to the whole
 * corpus is a report that lies about its own population, which is the failure mode this module
 * spends its scoping rules avoiding.
 */
object SetMembership {

    /**
     * Scryfall's set codes are 3–6 characters — 1,047 sets, none shorter (checked, not assumed). A
     * shorter string is a prefix someone is still typing into the explorer's set box, and answering
     * it locally is what stops a debounced text field firing a 404 at Scryfall per keystroke.
     */
    private val CODE = Regex("[A-Za-z0-9]{3,6}")

    /** Same window the Oracle bulk uses. A released set is immutable; one in spoiler season is not. */
    private val FRESH_FOR: Duration = Duration.ofDays(7)

    private val CACHE_ROOT = File(System.getProperty("user.home"), ".cache/scryfall")

    private const val SEARCH = "https://api.scryfall.com/cards/search"

    /** Scryfall asks for 50–100 ms between requests. A set is one or two pages, so this is cheap. */
    private const val PAGE_PAUSE_MS = 100L

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Memoized including the misses, so a bad code costs one round trip per process rather than one
     * per request. The computation runs under the map's per-bin lock, which is deliberate: two
     * threads asking for the same set should wait for one fetch, not race two.
     */
    private val memo = ConcurrentHashMap<String, Lookup>()

    /** The cards printed in [code], or `null` when Scryfall knows no such set and none is cached. */
    fun of(code: String, refresh: Boolean = false): SetCards? {
        val key = code.trim().lowercase(Locale.ROOT)
        if (refresh) memo.remove(key)
        return memo.computeIfAbsent(key) { Lookup(load(it, refresh)) }.cards
    }

    fun cacheFile(code: String): File =
        File(CACHE_ROOT, "_setlist-${code.trim().lowercase(Locale.ROOT)}.tsv")

    private class Lookup(val cards: SetCards?)

    private fun load(code: String, refresh: Boolean): SetCards? {
        if (!CODE.matches(code)) return null
        val cache = cacheFile(code)
        if (!refresh && cache.isFile && isFresh(cache)) return read(code, cache)

        val fetched = try {
            fetch(code) ?: return null // Scryfall knows the query and it matched nothing: no such set.
        } catch (e: Exception) {
            if (cache.isFile) {
                System.err.println("assay: could not reach Scryfall for set $code ($e); using the cached list")
                return read(code, cache)
            }
            System.err.println("assay: could not reach Scryfall for set $code ($e) and nothing is cached")
            return null
        }
        write(cache, fetched)
        return fetched
    }

    private fun isFresh(file: File): Boolean =
        Instant.ofEpochMilli(file.lastModified()).isAfter(Instant.now().minus(FRESH_FOR))

    /**
     * `unique=cards` rather than `unique=prints`: a set's variations and alternate frames are the
     * same card for a membership test, and collapsing them halves the pages.
     */
    private fun fetch(code: String): SetCards? {
        val cards = LinkedHashMap<String, SetCard>()
        var url: String? = "$SEARCH?q=${URLEncoder.encode("set:$code", StandardCharsets.UTF_8)}&unique=cards"
        var page = 0
        while (url != null) {
            if (page++ > 0) Thread.sleep(PAGE_PAUSE_MS)
            val payload = try {
                json.parseToJsonElement(ScryfallHttp.get(url)).jsonObject
            } catch (e: ScryfallHttpException) {
                if (e.status == 404) return null else throw e
            }
            for (entry in (payload["data"] as? JsonArray)?.filterIsInstance<JsonObject>().orEmpty()) {
                val name = entry.str("name") ?: continue
                cards.putIfAbsent(name.lowercase(Locale.ROOT), SetCard(entry.str("oracle_id"), name))
            }
            url = payload.str("next_page")?.takeIf { (payload["has_more"] as? JsonPrimitive)?.content == "true" }
        }
        return if (cards.isEmpty()) null else SetCards(code, cards.values.toList())
    }

    private fun read(code: String, file: File): SetCards? {
        val cards = runCatching {
            file.readLines().mapNotNull { line ->
                if (line.isBlank()) return@mapNotNull null
                val name = line.substringAfter('\t', "").ifEmpty { return@mapNotNull null }
                SetCard(line.substringBefore('\t').ifEmpty { null }, name)
            }
        }.getOrElse { return null }
        return if (cards.isEmpty()) null else SetCards(code, cards)
    }

    /** Temp-and-rename, so an interrupted write cannot leave a half list that later runs trust. */
    private fun write(file: File, cards: SetCards) {
        runCatching {
            file.parentFile.mkdirs()
            val tmp = File(file.parentFile, "${file.name}.part")
            tmp.writeText(cards.cards.joinToString("\n") { "${it.oracleId.orEmpty()}\t${it.name}" })
            if (file.exists()) file.delete()
            check(tmp.renameTo(file)) { "could not move $tmp into place at $file" }
        }
        // A cache that cannot be written is a slower next run, never a failed one.
    }

    private fun JsonObject.str(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
}

/** One card as a set list records it: Scryfall's Oracle ID, plus the name the join falls back to. */
data class SetCard(val oracleId: String?, val name: String)

/** The cards printed in one set, indexed for the membership test [contains]. */
class SetCards internal constructor(val code: String, val cards: List<SetCard>) {

    private val oracleIds: Set<String> = cards.mapNotNullTo(HashSet()) { it.oracleId }

    private val names: Set<String> = HashSet<String>().apply {
        for (card in cards) {
            val lower = card.name.lowercase(Locale.ROOT)
            add(lower)
            add(lower.substringBefore(" // "))
        }
    }

    val size: Int get() = cards.size

    /**
     * Oracle ID first, name as the fallback — the same join [com.wingedsheep.assay.gate.Differential]
     * uses, for the same reason: the corpus's `oracle_id` is nullable, and a few layouts carry it per
     * face rather than on the card. Both sides are matched on the front-face name too, so a corpus
     * entry spelled `A // B` still joins a set list that spells only `A`.
     */
    fun contains(oracleId: String?, name: String): Boolean {
        if (oracleId != null && oracleId in oracleIds) return true
        val lower = name.lowercase(Locale.ROOT)
        return lower in names || lower.substringBefore(" // ") in names
    }

    fun contains(card: OracleCard): Boolean = contains(card.oracleId, card.name)
}
