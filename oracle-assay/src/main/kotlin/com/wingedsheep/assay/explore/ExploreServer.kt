package com.wingedsheep.assay.explore

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
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
 * that was never printed runs the identical `Touchstone` path a corpus card runs, normalization and
 * invertibility check included, instead of an approximation of it.
 *
 * ## What is left in this file
 *
 * Bytes, and nothing else. Every route's behaviour lives in [ExploreApi], because `game-server`
 * serves the same page and the same routes under a prefix so the Set Completion view can frame the
 * live tool — and two transports over one explorer is only true while neither of them decides
 * anything. If you find yourself adding a `when` on a route name here, it belongs there.
 *
 * The dependency rule holds: this is `com.sun.net.httpserver`, in the JDK, so `:mtg-sdk` is still
 * the module's only production dependency.
 */
class ExploreServer(private val port: Int, refresh: Boolean = false) {

    private val api = ExploreApi(refresh)

    fun start(): Int {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
        server.executor = Executors.newFixedThreadPool(4)

        server.createContext("/") { exchange -> exchange.respond { api.page() } }
        for (route in api.getRoutes) {
            server.createContext("/api/$route") { exchange ->
                exchange.json { api.get(route) { name -> exchange.param(name) } }
            }
        }
        for (route in api.postRoutes) {
            server.createContext("/api/$route") { exchange ->
                val body = exchange.requestBody.readBytes().toString(StandardCharsets.UTF_8)
                exchange.json { api.post(route, body) }
            }
        }

        server.start()
        api.startSweep()
        return server.address.port
    }

    private fun HttpExchange.param(name: String): String? {
        val query = requestURI.rawQuery ?: return null
        return query.split("&")
            .firstOrNull { it.substringBefore("=") == name }
            ?.substringAfter("=", "")
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
    }

    private fun HttpExchange.json(body: () -> kotlinx.serialization.json.JsonElement) {
        responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        send(api.encode(body).toByteArray(StandardCharsets.UTF_8))
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
}
