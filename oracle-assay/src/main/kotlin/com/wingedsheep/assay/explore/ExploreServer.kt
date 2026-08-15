package com.wingedsheep.assay.explore

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import com.wingedsheep.assay.corpus.SetMembership
import com.wingedsheep.assay.gate.Differential
import com.wingedsheep.assay.gate.DifferentialReport
import com.wingedsheep.assay.gate.Touchstone
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

/**
 * `assay explore` — the grammar and both gates, in a browser.
 *
 * ## Why a server and not a self-contained page
 *
 * The obvious alternative is what the mtgish model explorer had to do: precompute everything, embed
 * it in one HTML file, and compile the parser to WebAssembly so a custom card can still be parsed.
 * That shape exists because mtgish's parser is Go in another repository — the page could not call
 * it, so it had to carry a copy.
 *
 * Assay is ours and it is already on the classpath, so the same page can call the **live grammar**.
 * That is not a convenience, it is the difference between two tools: a page pinned to a build shows
 * what the grammar did at some commit, while this one shows what the rule you are editing does now.
 * A decline you are trying to fix is one restart away from being re-measured, and `parse` on text
 * that was never printed runs the identical [Touchstone] path a corpus card runs, normalization and
 * invertibility check included, instead of an approximation of it.
 *
 * ## Cost model
 *
 * Two things are expensive and both are done once. The corpus **sweep** ([AssayIndex]) runs at
 * startup on a background thread, so the page opens immediately and shows its progress; the
 * **differential** runs on first request to its own page and is then cached, because it decodes
 * 8,874 goldens and most sessions never ask for it. Everything else — a card, a custom parse, the
 * rule tree — is computed per request in milliseconds.
 *
 * The dependency rule holds: this is `com.sun.net.httpserver`, in the JDK, so `:mtg-sdk` is still
 * the module's only production dependency.
 */
class ExploreServer(private val port: Int, private val refresh: Boolean = false) {

    private val touchstone = Touchstone()
    private val json = Json { prettyPrint = false }

    @Volatile private var index: AssayIndex? = null
    @Volatile private var goldens: GoldenIndex = GoldenIndex.load()
    @Volatile private var swept = 0
    @Volatile private var failure: String? = null

    /** Guarded by [differentialLock]; computed on first request because it is the expensive one. */
    private var differential: DifferentialReport? = null
    private val differentialLock = Any()

    fun start(): Int {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
        server.executor = Executors.newFixedThreadPool(4)

        server.createContext("/") { exchange -> exchange.respond { page() } }
        server.createContext("/api/status") { exchange -> exchange.json { status() } }
        server.createContext("/api/overview") { exchange -> exchange.json { ready { Views.overview(it, goldens) } } }
        server.createContext("/api/search") { exchange ->
            exchange.json { ready { Views.search(it, exchange.param("q").orEmpty()) } }
        }
        server.createContext("/api/cards") { exchange -> exchange.json { cards(exchange) } }
        server.createContext("/api/card") { exchange -> exchange.json { card(exchange) } }
        server.createContext("/api/declines") { exchange ->
            exchange.json {
                ready {
                    Views.declines(
                        index = it,
                        ranking = exchange.ranking(),
                        query = exchange.param("q"),
                        limit = exchange.param("limit")?.toIntOrNull() ?: 100,
                    )
                }
            }
        }
        server.createContext("/api/decline") { exchange -> exchange.json { decline(exchange) } }
        server.createContext("/api/grammar") { exchange -> exchange.json { Views.grammar(index) } }
        server.createContext("/api/parse") { exchange -> exchange.json { parse(exchange) } }
        server.createContext("/api/differential") { exchange -> exchange.json { differential() } }

        server.start()
        Thread({ sweep() }, "assay-explore-sweep").apply { isDaemon = true }.start()
        return server.address.port
    }

    // -------------------------------------------------------------------------------------------
    // The one-time sweep
    // -------------------------------------------------------------------------------------------

    private fun sweep() {
        try {
            index = AssayIndex.build(refresh = refresh) { swept = it }
        } catch (e: Exception) {
            // A sweep that cannot start — no cached bulk file and no network — must leave the page
            // usable rather than a dead socket: the live parser and the rule tree need no corpus.
            failure = e.message ?: e::class.simpleName ?: "the corpus sweep failed"
        }
    }

    private fun status(): JsonObject = JsonObject(
        mapOf(
            "ready" to JsonPrimitive(index != null),
            "swept" to JsonPrimitive(index?.report?.cards ?: swept),
            "goldens" to JsonPrimitive(goldens.size),
            "error" to JsonPrimitive(failure ?: ""),
        )
    )

    /** Every corpus-backed endpoint answers "still sweeping" rather than blocking a request thread. */
    private fun ready(body: (AssayIndex) -> JsonElement): JsonElement {
        val current = index ?: return JsonObject(
            mapOf(
                "indexing" to JsonPrimitive(true),
                "swept" to JsonPrimitive(swept),
                "error" to JsonPrimitive(failure ?: ""),
            )
        )
        return body(current)
    }

    // -------------------------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------------------------

    private fun cards(exchange: HttpExchange): JsonElement = ready { index ->
        val set = exchange.param("set")?.takeIf { it.isNotBlank() }
        val filter = CardFilter(
            state = exchange.param("state"),
            set = set,
            // Resolved here rather than during the sweep, and on the request thread. A set list is
            // ~200 KB and then memoized for the process, so exactly one request per set pays for it;
            // pre-loading every set at startup would download 1,047 lists to serve a filter most
            // sessions never touch, and baking membership into the sweep would make the corpus
            // depend on the network for a question only this filter asks.
            setCards = set?.let { SetMembership.of(it) },
            query = exchange.param("q")?.takeIf { it.isNotBlank() },
            scopeOnly = exchange.param("scope") == "1",
            goldenOnly = exchange.param("golden") == "1",
            goldens = index.goldenNames,
        )
        Views.cards(
            index = index,
            filter = filter,
            offset = exchange.param("offset")?.toIntOrNull() ?: 0,
            limit = (exchange.param("limit")?.toIntOrNull() ?: 100).coerceIn(1, 500),
        )
    }

    private fun card(exchange: HttpExchange): JsonElement = ready { index ->
        val name = exchange.param("name").orEmpty()
        Views.cardPage(index, goldens, touchstone, name)
            ?: JsonObject(mapOf("error" to JsonPrimitive("no card named \"$name\" in the Oracle bulk")))
    }

    private fun decline(exchange: HttpExchange): JsonElement = ready { index ->
        val token = exchange.param("token").orEmpty()
        Views.decline(index, exchange.ranking(), token)
            ?: JsonObject(mapOf("error" to JsonPrimitive("no decline family \"$token\"")))
    }

    private fun HttpExchange.ranking(): Ranking =
        if (param("by") == "shape") Ranking.SHAPE else Ranking.TOKEN

    private fun parse(exchange: HttpExchange): JsonElement {
        val body = exchange.requestBody.readBytes().toString(StandardCharsets.UTF_8)
        val fields = runCatching { json.parseToJsonElement(body) as JsonObject }.getOrNull()
            ?: return JsonObject(mapOf("error" to JsonPrimitive("expected a JSON object")))
        fun field(key: String) = (fields[key] as? JsonPrimitive)?.content.orEmpty()
        return Views.parsed(
            touchstone,
            ParseRequest(
                name = field("name"),
                manaCost = field("manaCost"),
                typeLine = field("typeLine"),
                oracleText = field("oracleText"),
            ),
        )
    }

    private fun differential(): JsonElement = ready { index ->
        if (!goldens.available) {
            return@ready JsonObject(
                mapOf(
                    "error" to JsonPrimitive(
                        "no hand-written card goldens found — run `just test-class CardDefinitionSnapshotTest`"
                    )
                )
            )
        }
        val report = synchronized(differentialLock) {
            differential ?: Differential(touchstone).run().also { differential = it }
        }
        Views.differential(report)
    }

    // -------------------------------------------------------------------------------------------
    // Plumbing
    // -------------------------------------------------------------------------------------------

    private fun page(): ByteArray =
        javaClass.getResourceAsStream(PAGE)?.readBytes()
            ?: error("$PAGE is missing from the jar — the explorer page is a resource of :oracle-assay")

    private fun HttpExchange.param(name: String): String? {
        val query = requestURI.rawQuery ?: return null
        return query.split("&")
            .firstOrNull { it.substringBefore("=") == name }
            ?.substringAfter("=", "")
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
    }

    private fun HttpExchange.json(body: () -> JsonElement) {
        val payload = runCatching { json.encodeToString(JsonElement.serializer(), body()) }
            .getOrElse { e ->
                // A handler that throws must produce a readable error in the page rather than an
                // empty response the browser reports as a network failure.
                """{"error":${JsonPrimitive(e.message ?: e::class.simpleName ?: "internal error")}}"""
            }
        responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        send(payload.toByteArray(StandardCharsets.UTF_8))
    }

    private fun HttpExchange.respond(body: () -> ByteArray) {
        responseHeaders.add("Content-Type", "text/html; charset=utf-8")
        send(body())
    }

    private fun HttpExchange.send(bytes: ByteArray) {
        try {
            // Nothing here is cacheable: the whole point is that a restart re-measures against an
            // edited grammar, and a browser holding yesterday's page or payload would silently
            // undo that.
            responseHeaders.add("Cache-Control", "no-store")
            sendResponseHeaders(200, bytes.size.toLong())
            responseBody.use { it.write(bytes) }
        } catch (_: IOException) {
            // The browser navigated away mid-response. Not worth a stack trace on the console.
        }
    }

    private companion object {
        const val PAGE = "/explorer/index.html"
    }
}
