package com.wingedsheep.gameserver.replay

import com.wingedsheep.gameserver.persistence.persistenceJson
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Encodes replay payloads for durable storage as compactly as possible: gzip, then base64.
 *
 * Both stored payloads are JSON and both are highly repetitive — the input stream repeats `"type"`
 * discriminators and entity-id strings, the archived presentation stream repeats whole card objects
 * frame after frame — so gzip typically shaves 80–95%. base64 keeps them portable TEXT columns
 * across databases (no `bytea` round-tripping in Spring Data JDBC).
 *
 * Decoding is deliberately tolerant: [persistenceJson] ignores unknown keys and every field added to
 * [CompactReplay] since v1 has a default, so a record written by a newer build stays readable by an
 * older one — the situation you are in for the minutes a rolling deploy takes.
 */
object ReplayCodec {

    fun encode(replay: CompactReplay): String =
        encodeText(persistenceJson.encodeToString(CompactReplay.serializer(), replay))

    fun decode(encoded: String): CompactReplay =
        persistenceJson.decodeFromString(CompactReplay.serializer(), decodeText(encoded))

    /** gzip + base64 an arbitrary JSON payload (used for the archived presentation stream). */
    fun encodeText(text: String): String {
        val gzipped = ByteArrayOutputStream().also { out ->
            GZIPOutputStream(out).use { it.write(text.toByteArray(Charsets.UTF_8)) }
        }.toByteArray()
        return Base64.getEncoder().encodeToString(gzipped)
    }

    fun decodeText(encoded: String): String =
        GZIPInputStream(ByteArrayInputStream(Base64.getDecoder().decode(encoded))).use {
            it.readBytes().toString(Charsets.UTF_8)
        }
}
