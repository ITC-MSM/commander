package com.wingedsheep.assay.cli

import com.wingedsheep.assay.corpus.ImplementedCorpus
import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleCorpus
import com.wingedsheep.assay.corpus.SetMembership
import com.wingedsheep.assay.gate.Differential
import com.wingedsheep.assay.gate.FinenessReport
import com.wingedsheep.assay.gate.LineVerdict
import com.wingedsheep.assay.gate.Touchstone
import com.wingedsheep.assay.explore.ExploreServer
import com.wingedsheep.assay.syntax.explain
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.serialization.CardSerialization
import kotlin.system.exitProcess

/**
 * `assay` — the command line.
 *
 * ```
 * assay parse "Serra Angel"      normalized lines, the model each parses to, and the printed form
 * assay explain "Wall of Omens"  the same, but showing the token each decline died on
 * assay gate                     the touchstone over the whole corpus; non-zero exit on a bug
 * assay report                   the same numbers, always exit 0 — for reading, not gating
 * assay differential             Assay's readings vs. the hand-written cards (gate 2)
 * assay explore                  the same material in a browser, against the live grammar
 * assay corpus --refresh         re-download the Scryfall Oracle bulk
 * ```
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        usage()
        exitProcess(2)
    }
    val command = args.first()
    val rest = args.drop(1)
    val flags = Flags(rest)

    when (command) {
        "parse" -> exitProcess(parse(flags, explainDeclines = false))
        "explain" -> exitProcess(parse(flags, explainDeclines = true))
        "gate" -> exitProcess(gate(flags, gating = true))
        "report" -> exitProcess(gate(flags, gating = false))
        "differential" -> exitProcess(differential(flags))
        "explore" -> exitProcess(explore(flags))
        "corpus" -> exitProcess(corpus(flags))
        "-h", "--help", "help" -> {
            usage()
            exitProcess(0)
        }

        else -> {
            System.err.println("assay: unknown command '$command'")
            usage()
            exitProcess(2)
        }
    }
}

private fun usage() = System.err.println(
    """
    Argentum Assay — first-party Oracle-text parser (docs/oracle-assay.md)

      assay parse <card name>        parse one card and print its model
      assay explain <card name>      parse one card, showing where declines died
      assay gate [options]           run the touchstone; exits 1 on ambiguity/mismatch
      assay report [options]         the same report, always exits 0
      assay differential [options]   diff Assay's readings against the hand-written cards
      assay explore [--port N]       browse all of the above against the live grammar
      assay corpus [--refresh]       show or refresh the cached Scryfall Oracle bulk

    Options:
      --limit N        assay only the first N cards (a fast smoke run)
      --set CODE       restrict to one set — every card *printed* in it for gate/report (a small
                       per-set list is fetched from Scryfall and cached), the golden's file name
                       for differential
      --scope          restrict to vanilla + keyword-only cards — Phase 1's own target, so the
                       decline table becomes exactly the list of what is blocking that number
      --implemented    restrict to cards that already have a hand-written golden, so the decline
                       table becomes the *grammar* backlog: gaps whose answer is already written
                       and which the differential confirms the moment they parse
      --top N          how many decline families (or divergences) to list
      --refresh        re-download the bulk file before running
      --declines       after the report, list every declined line (long)
      --port N         explore: port to serve on (0 picks a free one; default 7345)
      --no-open        explore: do not open a browser
    """.trimIndent()
)

private class Flags(args: List<String>) {
    private val positional = mutableListOf<String>()
    private val named = mutableMapOf<String, String?>()

    init {
        var i = 0
        while (i < args.size) {
            val arg = args[i]
            if (arg.startsWith("--")) {
                val key = arg.removePrefix("--")
                val next = args.getOrNull(i + 1)
                if (next != null && !next.startsWith("--")) {
                    named[key] = next
                    i++
                } else {
                    named[key] = null
                }
            } else {
                positional.add(arg)
            }
            i++
        }
    }

    val rest: String get() = positional.joinToString(" ")
    fun has(name: String) = name in named
    fun int(name: String): Int? = named[name]?.toIntOrNull()
    fun str(name: String): String? = named[name]
}

private fun corpus(flags: Flags): Int {
    val refresh = flags.has("refresh")
    if (!refresh && OracleCorpus.isCached()) {
        val file = OracleCorpus.cacheFile()
        println("cached: $file (${file.length() / 1024 / 1024} MB)")
        println("run `assay corpus --refresh` to re-download")
        return 0
    }
    val count = OracleCorpus.cards(refresh = refresh).count()
    println("corpus: $count assayable cards at ${OracleCorpus.cacheFile()}")
    return 0
}

private fun parse(flags: Flags, explainDeclines: Boolean): Int {
    val wanted = flags.rest.trim()
    if (wanted.isEmpty()) {
        System.err.println("assay: give a card name, e.g. assay parse \"Serra Angel\"")
        return 2
    }
    val card = findCard(wanted) ?: run {
        System.err.println("assay: no card named '$wanted' in the Oracle bulk")
        return 1
    }

    val result = Touchstone().assay(card)
    println(card.name)
    println("  layout ${card.layout}   Scryfall keywords: ${card.scryfallKeywords.ifEmpty { listOf("—") }}")
    println("  in Phase 1 scope (vanilla or keyword-only): ${result.inPhase1Scope}")
    for (face in result.faces) {
        if (result.faces.size > 1) println("\n  face: ${face.faceName}")
        if (!face.normalizationHolds) println("  !! normalization is not invertible for this face")
        if (face.lines.isEmpty() || face.normalized.isVanilla) println("  (vanilla — no rules text)")
        for (line in face.lines) {
            println("\n  line ${line.index}: ${line.line.ifEmpty { "(empty)" }}")
            println("    verdict: ${line.verdict}")
            line.model?.keywordAbilities?.forEach {
                println("    model:   ${it::class.simpleName}(${it.description})")
            }
            line.model?.script?.takeIf { it != CardScript.EMPTY }?.let {
                println("    script:  ${CardSerialization.json.encodeToString(CardScript.serializer(), it)}")
            }
            if (line.printed != null && line.printed != line.line) println("    printed: ${line.printed}")
            val decline = line.decline
            if (explainDeclines && decline != null) {
                decline.explain(line.line).lines().forEach { println("    $it") }
            }
        }
        face.glosses.forEach {
            println("\n  reminder gloss for '${it.keyword}': ${it.verdict}")
            if (it.regenerated != null && it.regenerated != it.printed) {
                println("    printed:     ${it.printed}")
                println("    regenerated: ${it.regenerated}")
            }
        }
    }
    // parse/explain are inspection commands: a declined card is information, not a failing run.
    // `assay gate` is the thing that exits non-zero, and only on a bug.
    return 0
}

/**
 * Golden headers carry Scryfall's name verbatim, so the full name is the match. The front-face
 * fallback covers the handful of goldens filed under one half of a split card's name.
 */
private fun isImplemented(card: OracleCard, implemented: Set<String>): Boolean =
    card.name.lowercase() in implemented || card.name.substringBefore(" // ").lowercase() in implemented

private fun findCard(name: String): OracleCard? {
    val wanted = name.lowercase()
    return OracleCorpus.cards().firstOrNull {
        it.name.lowercase() == wanted ||
            it.name.substringBefore(" // ").lowercase() == wanted ||
            it.faces.any { face -> face.name.lowercase() == wanted }
    }
}

/**
 * Gate 2. Exits non-zero only on a golden that will not decode — never on a divergence, which is a
 * finding to classify rather than a build break (see [com.wingedsheep.assay.gate.DifferentialReport.clean]).
 */
private fun differential(flags: Flags): Int {
    if (!ImplementedCorpus.isAvailable()) {
        System.err.println(
            "assay: no hand-written card goldens at ${ImplementedCorpus.snapshotDir()} — " +
                "run `just test-class CardDefinitionSnapshotTest` to generate them"
        )
        return 2
    }
    val report = Differential().run(
        refresh = flags.has("refresh"),
        limit = flags.int("limit"),
        setFilter = flags.str("set"),
    )
    println(report.render(topDivergences = flags.int("top") ?: 40))
    if (report.clean) return 0
    System.err.println("assay: differential FAILED — a golden would not decode")
    return 1
}

/**
 * The explorer. Serves on loopback only and blocks until interrupted.
 *
 * The corpus sweep runs on a background thread inside [ExploreServer], so the page is up before the
 * numbers are — deliberately, since the live parser and the rule tree need no corpus at all and are
 * useful in the five seconds the sweep takes.
 */
private fun explore(flags: Flags): Int {
    val requested = flags.int("port") ?: DEFAULT_EXPLORE_PORT
    val port = try {
        ExploreServer(requested, refresh = flags.has("refresh")).start()
    } catch (e: java.io.IOException) {
        System.err.println("assay: could not serve on port $requested (${e.message}) — try --port 0")
        return 1
    }
    val url = "http://127.0.0.1:$port/"
    println("Argentum Assay — explorer at $url")
    println("Indexing the corpus in the background; the page shows its progress. Ctrl-C to stop.")
    if (!flags.has("no-open")) openBrowser(url)
    // The HttpServer's threads are daemons' peers, not the process lifetime — park the main thread.
    Thread.currentThread().join()
    return 0
}

/** Best-effort. A browser that will not open is a printed URL, never an error. */
private fun openBrowser(url: String) {
    val command = when {
        System.getProperty("os.name").orEmpty().startsWith("Mac") -> listOf("open", url)
        System.getProperty("os.name").orEmpty().startsWith("Windows") -> listOf("cmd", "/c", "start", url)
        else -> listOf("xdg-open", url)
    }
    runCatching { ProcessBuilder(command).start() }
}

private const val DEFAULT_EXPLORE_PORT = 7345

private fun gate(flags: Flags, gating: Boolean): Int {
    val touchstone = Touchstone()
    val builder = FinenessReport.builder()
    val setFilter = flags.str("set")?.uppercase()
    val limit = flags.int("limit")

    // Membership, not the corpus's `setCode` — that field is a card's *representative* printing, so
    // filtering on it silently drops most of any old or heavily reprinted set. See [SetMembership].
    val setCards = setFilter?.let {
        SetMembership.of(it, refresh = flags.has("refresh")) ?: run {
            System.err.println(
                "assay: no set \"$it\" — Scryfall knows no such code, or it is unreachable and " +
                    "nothing is cached at ${SetMembership.cacheFile(it)}"
            )
            return 2
        }
    }

    val declineLines = mutableListOf<String>()

    val scopeOnly = flags.has("scope")

    // `--implemented` splits the decline list by whether someone has already written the card.
    // A declined line on a card that has a golden is a *grammar* gap whose known-good answer is
    // sitting in the goldens, and the differential confirms it the moment it parses; a declined
    // line on a card nobody has implemented may be an *SDK* gap, which is add-feature work with a
    // far longer lead time. Ranked together they are one undifferentiated list.
    val implementedOnly = flags.has("implemented")
    if (implementedOnly && !ImplementedCorpus.isAvailable()) {
        System.err.println(
            "assay: no hand-written card goldens at ${ImplementedCorpus.snapshotDir()} — " +
                "run `just test-class CardDefinitionSnapshotTest` to generate them"
        )
        return 2
    }
    val implemented = if (implementedOnly) ImplementedCorpus.names().mapTo(HashSet()) { it.lowercase() } else emptySet()

    var seen = 0
    for (card in OracleCorpus.cards(refresh = flags.has("refresh"))) {
        if (setCards != null && !setCards.contains(card)) continue
        if (implementedOnly && !isImplemented(card, implemented)) continue
        val result = touchstone.assay(card)
        if (scopeOnly && !result.inPhase1Scope) continue
        builder.add(result)
        if (flags.has("declines")) {
            result.lines.filter { it.verdict == LineVerdict.DECLINED }
                .forEach { declineLines.add("${card.name}: ${it.line}") }
        }
        seen++
        if (limit != null && seen >= limit) break
    }

    val report = builder.build()
    val population = listOfNotNull(
        "cards with a hand-written golden".takeIf { implementedOnly },
        "vanilla + keyword-only (Phase 1 scope)".takeIf { scopeOnly },
        setCards?.let { "set ${it.code.uppercase()} — ${it.size} cards printed in it" },
    ).joinToString(" · ").ifEmpty { null }
    println(report.render(topDeclines = flags.int("top") ?: 20, population = population))

    if (declineLines.isNotEmpty()) {
        println()
        println("EVERY DECLINED LINE")
        println("-".repeat(78))
        declineLines.forEach { println("  $it") }
    }

    if (!gating) return 0
    return if (report.clean) {
        0
    } else {
        System.err.println("assay: gate FAILED — ambiguity, print mismatch, or non-invertible normalization")
        1
    }
}
