package com.wingedsheep.gameserver.replay

import com.wingedsheep.gameserver.persistence.persistenceJson
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
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

    /**
     * The pinned card definitions, encoded for their own write-once column rather than folded into
     * [encode]'s blob — they are the largest part of a record and the only part that never changes,
     * so keeping them out of the per-flush payload is what stops a long game rewriting them a few
     * hundred times. Null for a record with no pins, so the column stays honestly empty.
     */
    fun encodePins(pins: List<String>): String? =
        if (pins.isEmpty()) null
        else encodeText(persistenceJson.encodeToString(ListSerializer(String.serializer()), pins))

    fun decodePins(encoded: String?): List<String> =
        encoded?.let { persistenceJson.decodeFromString(ListSerializer(String.serializer()), decodeText(it)) }
            ?: emptyList()
}
