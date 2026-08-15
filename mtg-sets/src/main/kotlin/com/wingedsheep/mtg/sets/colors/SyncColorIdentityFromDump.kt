package com.wingedsheep.mtg.sets.colors

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Offline sync against a Scryfall bulk-data dump (the "All Cards" or "Default Cards" JSON).
 * Streams every printing, builds a `name -> color_identity` map, then walks every card
 * definition file under every card module's `definitions/.../cards/` — the corpus is split across
 * `:mtg-sets:core` and the `:mtg-sets:<era>` modules — and rewrites each in place to add or update
 * an explicit `colorIdentity = "..."` line in the `card(...) { ... }` builder block.
 *
 * Run: `./gradlew :mtg-sets:syncColorIdentityFromDump --args="/path/to/all-cards-YYYYMMDDhhmmss.json"`.
 *
 * Why inline (rather than a side-loaded JSON resource): the user explicitly wanted the value to
 * live next to the card definition so it's reviewable, greppable, and stable across rebuilds. The
 * heuristic in [com.wingedsheep.sdk.model.CardDefinition.colorIdentity] still applies for any
 * card whose file the sync didn't touch (custom playtest cards, brand-new cards not yet in the
 * dump, etc.), so behaviour degrades gracefully when a card is missing from Scryfall.
 *
 * The literal we write is a string of canonical-order color symbols (`WUBRG`), e.g. `"WB"` for
 * Orzhov, `""` for explicitly colorless. The DSL setter parses the string into a `Set<Color>`
 * at build time.
 */
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val dumpArg = args.firstOrNull()
        ?: error("Usage: syncColorIdentityFromDump <path-to-scryfall-bulk-cards.json>")
    val dumpPath = Paths.get(dumpArg)
    require(Files.exists(dumpPath)) { "Dump not found: $dumpPath" }

    // Card definitions live in every `mtg-sets/` submodule that has a definitions/ tree, so a new
    // era module is picked up without touching this task. Run from the repo root (the Gradle task
    // sets workingDir accordingly).
    val setsRoot = Paths.get("mtg-sets")
    val cardsRoots = Files.list(setsRoot).use { stream ->
        stream
            .map { it.resolve("src/main/kotlin/com/wingedsheep/mtg/sets/definitions") }
            .filter { Files.isDirectory(it) }
            .toList()
    }
    require(cardsRoots.isNotEmpty()) { "No card definition modules found under ${setsRoot.toAbsolutePath()}" }

    println("Scanning $dumpPath for color identities…")
    val parser = Json { ignoreUnknownKeys = true; isLenient = false }

    // Same name across printings agrees on color_identity (it's an oracle-level property), so
    // first-write-wins is fine. Index both the joined name "Front // Back" and the front face
    // alone so a CardDefinition carrying just "Front" still resolves.
    val identities = HashMap<String, String>()
    var scanned = 0L
    Files.newInputStream(dumpPath).use { stream ->
        val cards = parser.decodeToSequence(stream, ScryfallDumpCard.serializer())
        for (card in cards) {
            scanned++
            val rawName = card.name?.trim().orEmpty()
            if (rawName.isBlank()) continue
            val literal = canonicalize(card.color_identity)
            val frontFace = rawName.substringBefore(" // ").trim()
            identities.putIfAbsent(rawName, literal)
            if (frontFace != rawName) identities.putIfAbsent(frontFace, literal)
            if (scanned % 200_000 == 0L) {
                println("  scanned $scanned printings (${identities.size} unique names)")
            }
        }
    }
    println("Indexed ${identities.size} unique card names from $scanned printings.")

    val cardFiles = cardsRoots.flatMap { root ->
        Files.walk(root).use { stream ->
            stream
                .filter { Files.isRegularFile(it) }
                .filter { it.fileName.toString().endsWith(".kt") }
                .filter { !it.fileName.toString().endsWith("Set.kt") }
                .toList()
        }
    }

    var patched = 0
    var alreadyCurrent = 0
    var notACard = 0
    val unknown = mutableListOf<String>()
    val multiCard = mutableListOf<Path>()

    for (file in cardFiles) {
        val original = Files.readString(file)
        val matches = CARD_HEADER.findAll(original).toList()
        if (matches.isEmpty()) {
            // basicLand(...) helper or non-card file — leave alone.
            notACard++
            continue
        }
        if (matches.size > 1) {
            // Multi-card files are rare; skip and report rather than risk a bad rewrite.
            multiCard.add(file)
            continue
        }
        val match = matches.single()
        val cardName = match.groupValues[1]
        val literal = identities[cardName]
        if (literal == null) {
            unknown += cardName
            continue
        }
        val newContent = applyColorIdentity(original, literal)
        if (newContent == null) {
            // Couldn't find an insertion anchor — log and move on.
            unknown += "$cardName (no anchor)"
            continue
        }
        if (newContent == original) {
            alreadyCurrent++
        } else {
            Files.writeString(file, newContent)
            patched++
        }
    }

    println()
    println("Result:")
    println("  patched          : $patched")
    println("  already current  : $alreadyCurrent")
    println("  non-card files   : $notACard")
    println("  multi-card files : ${multiCard.size}  (left untouched)")
    println("  unknown to dump  : ${unknown.size}")
    if (multiCard.isNotEmpty()) {
        println()
        println("Multi-card files (need manual review):")
        multiCard.forEach { println("  - $it") }
    }
    if (unknown.isNotEmpty()) {
        println()
        println("Card names with no Scryfall match:")
        unknown.sorted().forEach { println("  - $it") }
    }
}

/**
 * Header regex matching the single `val <Name> = card("Card Name") {` declaration that opens a
 * card file. Anchored to the start of a line so it doesn't match strings inside other code.
 */
private val CARD_HEADER = Regex("""^val\s+\w+\s*=\s*card\(\s*"([^"]+)"\s*\)\s*\{""", RegexOption.MULTILINE)

/**
 * Matches an existing `colorIdentity = "..."` line at top level of the builder (4-space indent).
 * Captures the literal so we can detect "already current" vs "needs replacement".
 */
private val EXISTING_COLOR_LINE = Regex("""(?m)^( {4})colorIdentity\s*=\s*"([^"]*)"\s*$""")

/** Top-level `manaCost = "..."` — preferred insertion anchor. */
private val MANA_COST_LINE = Regex("""(?m)^( {4})manaCost\s*=\s*"[^"]*"\s*$""")

/** Fallback anchor for cards with no mana cost (lands). */
private val TYPE_LINE_LINE = Regex("""(?m)^( {4})typeLine\s*=\s*"[^"]*"\s*$""")

private fun canonicalize(raw: List<String>): String {
    val order = "WUBRG"
    val present = raw.asSequence()
        .map { it.trim().uppercase() }
        .mapNotNull { it.firstOrNull() }
        .filter { it in order }
        .toSet()
    return order.filter { it in present }
}

/**
 * Apply [identityLiteral] to [original]. Returns the rewritten content or `null` if no insertion
 * anchor (manaCost/typeLine) was found.
 */
private fun applyColorIdentity(original: String, identityLiteral: String): String? {
    val existing = EXISTING_COLOR_LINE.find(original)
    if (existing != null) {
        if (existing.groupValues[2] == identityLiteral) return original
        return original.substring(0, existing.range.first) +
            "${existing.groupValues[1]}colorIdentity = \"$identityLiteral\"" +
            original.substring(existing.range.last + 1)
    }
    val anchor = MANA_COST_LINE.find(original) ?: TYPE_LINE_LINE.find(original) ?: return null
    val insertAt = anchor.range.last + 1
    val indent = anchor.groupValues[1]
    return original.substring(0, insertAt) +
        "\n${indent}colorIdentity = \"$identityLiteral\"" +
        original.substring(insertAt)
}

@Serializable
private data class ScryfallDumpCard(
    val name: String? = null,
    val color_identity: List<String> = emptyList(),
)
