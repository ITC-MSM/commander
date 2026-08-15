package com.wingedsheep.assay.explore

import com.wingedsheep.assay.corpus.ImplementedCard
import com.wingedsheep.assay.corpus.ImplementedCorpus
import com.wingedsheep.sdk.serialization.CardLoader
import java.io.File
import java.util.Locale

/**
 * The hand-written goldens, indexed by name and decoded **one card at a time**.
 *
 * [ImplementedCorpus.cards] streams the whole corpus and decodes every entry, which is right for the
 * differential gate — it compares all of them — and wrong for a page that wants one. Decoding 8,874
 * `CardDefinition`s to answer "what did the human write for Serra Angel?" costs more than the entire
 * touchstone run.
 *
 * So this indexes the *text*: name to (set, raw JSON), which is a header scan over 20 MB and needs no
 * SDK deserialization at all. The definition is decoded on demand, when a card page asks for it.
 */
class GoldenIndex private constructor(private val entries: Map<String, Entry>) {

    private data class Entry(val name: String, val setCode: String, val json: String)

    val size: Int get() = entries.size

    val available: Boolean get() = entries.isNotEmpty()

    /**
     * The golden for [name], decoded now.
     *
     * A definition that will not decode comes back as an [ImplementedCard] with a null definition
     * rather than as an exception — the same "declining is success" shape the rest of the module
     * uses, and what lets the differential count an undecodable golden instead of dying on it.
     */
    fun card(name: String): ImplementedCard? {
        val entry = entries[key(name)] ?: entries[key(name.substringBefore(" // "))] ?: return null
        return ImplementedCard(
            name = entry.name,
            setCode = entry.setCode,
            definition = runCatching { CardLoader.fromJsonPreservingIds(entry.json) }.getOrNull(),
        )
    }

    fun json(name: String): String? =
        (entries[key(name)] ?: entries[key(name.substringBefore(" // "))])?.json

    private fun key(name: String) = name.lowercase(Locale.ROOT)

    companion object {

        fun load(): GoldenIndex {
            val dir = runCatching { ImplementedCorpus.snapshotDir() }.getOrNull()
            if (dir == null || !dir.isDirectory) return GoldenIndex(emptyMap())
            val files = dir.listFiles { f: File -> f.isFile && f.extension == "json" }?.sortedBy { it.name }
            val index = HashMap<String, Entry>()
            for (file in files.orEmpty()) {
                val setCode = file.nameWithoutExtension
                for ((name, json) in ImplementedCorpus.splitEntries(file.readText())) {
                    index.putIfAbsent(name.lowercase(Locale.ROOT), Entry(name, setCode, json))
                }
            }
            return GoldenIndex(index)
        }
    }
}
