package com.wingedsheep.mtg.sets

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File

private const val SET_CODE = "FEM"
private const val DUMP_RELATIVE = "sets/fallen-empires/fem_set.json"

/**
 * Field-level verification of every registered Fallen Empires card against authoritative Scryfall
 * data. Temporary — part of the verify-set run, deleted once it reaches zero.
 */
class FemCardFieldVerificationTest : FunSpec({

    val dump = Json.parseToJsonElement(dumpFile().readText()).jsonObject
    val scryfall = dump["data"]!!.jsonArray.map { it.jsonObject }
    val byCollector = scryfall.associateBy { it.str("collector_number") }
    val byId = scryfall.associateBy { it.str("id") }
    val set = MtgSetCatalog.requireByCode(SET_CODE)

    test("$SET_CODE: every registered card matches authoritative Scryfall on all requested fields") {
        val cards = set.cards.sortedBy { it.name }
        val problems = mutableListOf<String>()

        for (card in cards) {
            val cn = card.metadata.collectorNumber
            val a = byCollector[cn] ?: byId[card.scryfallId()]
            if (a == null) {
                problems += "${card.name} (cn=$cn): no authoritative Scryfall match"
                continue
            }

            check(problems, card.name, "color_identity", card.colorIdentityString(), a.colorIdentityString())
            check(problems, card.name, "rarity", card.metadata.rarity.scryfall(), a.str("rarity"))
            check(problems, card.name, "collector_number", cn ?: "", a.str("collector_number"))

            val ourFaces = if (card.isDoubleFaced) listOf(card, card.backFace!!) else listOf(card)
            val authFaces = a.faces()
            if (ourFaces.size != authFaces.size) {
                problems += "${card.name}: face-count mismatch ours=${ourFaces.size} auth=${authFaces.size}"
                continue
            }
            for ((idx, pair) in ourFaces.zip(authFaces).withIndex()) {
                val (ourFace, authFace) = pair
                val label = if (card.isDoubleFaced) "${card.name}[$idx:${ourFace.name}]" else card.name
                val nonmodalBack = card.isDoubleFaced && idx == 1 && ourFace.colorIndicator != null
                checkFace(problems, label, ourFace, authFace, skipManaCost = nonmodalBack)
            }
        }

        report(problems, "$SET_CODE cards", cards.size)
        problems shouldBe emptyList()
    }

    test("$SET_CODE: every reprint row matches its Scryfall printing") {
        val problems = mutableListOf<String>()
        for (p in set.printings.sortedBy { it.collectorNumber }) {
            val a = byCollector[p.collectorNumber] ?: byId[p.scryfallId]
            if (a == null) {
                problems += "${p.name} (cn=${p.collectorNumber}): no authoritative Scryfall match"
                continue
            }
            val label = "${p.name}#${p.collectorNumber}"
            check(problems, label, "name", p.name, a.faces().first().str("name"))
            check(problems, label, "rarity", p.rarity.scryfall(), a.str("rarity"))
            check(problems, label, "artist", p.artist, a.faces().first().str("artist"))
            check(problems, label, "image_uris", p.imageUri?.substringBefore("?"),
                a.faces().first().imageNormal()?.substringBefore("?"))
        }
        report(problems, "$SET_CODE reprints", set.printings.size)
        problems shouldBe emptyList()
    }

    test("$SET_CODE: every basic land maps to a real printing in the dump") {
        val problems = mutableListOf<String>()
        for (land in set.basicLands) {
            val cn = land.metadata.collectorNumber
            val a = byCollector[cn]
            when {
                a == null -> problems += "${land.name} (cn=$cn): no printing at that collector number"
                a.faces().first().str("name") != land.name ->
                    problems += "${land.name} (cn=$cn): collector number belongs to " +
                        "${a.faces().first().str("name")}"
            }
        }
        report(problems, "$SET_CODE basic lands", set.basicLands.size)
        problems shouldBe emptyList()
    }
})

private val WAIVED: Set<Pair<String, String>> = emptySet()

private fun checkFace(
    problems: MutableList<String>,
    label: String,
    def: CardDefinition,
    a: JsonObject,
    skipManaCost: Boolean = false,
) {
    check(problems, label, "name", def.name, a.str("name"))
    if (!skipManaCost) check(problems, label, "mana_cost", def.manaCost.toString(), a.str("mana_cost") ?: "")
    check(problems, label, "type_line", def.typeLine.toString(), a.str("type_line"))
    check(problems, label, "oracle_text", def.oracleText, a.str("oracle_text") ?: "")
    check(problems, label, "power", def.creatureStats?.power?.description, a.str("power"))
    check(problems, label, "toughness", def.creatureStats?.toughness?.description, a.str("toughness"))
    check(problems, label, "loyalty", def.startingLoyalty?.toString(), a.str("loyalty"))
    check(problems, label, "artist", def.metadata.artist, a.str("artist"))
    check(problems, label, "flavor_text", def.metadata.flavorText, a.str("flavor_text"))
    check(problems, label, "image_uris",
        def.metadata.imageUri?.substringBefore("?"), a.imageNormal()?.substringBefore("?"))
}

private fun check(problems: MutableList<String>, label: String, field: String, ours: String?, auth: String?) {
    if ((label to field) in WAIVED) return
    val o = canon(field, ours)
    val a = canon(field, auth)
    if (o != a) problems += "$label.$field: ours=${o.q()} auth=${a.q()}"
}

private fun canon(field: String, s: String?): String {
    val v = s?.trim().orEmpty()
    if (field == "power" || field == "toughness") {
        Regex("""^\*\+(\d+)$""").find(v)?.let { return "${it.groupValues[1]}+*" }
    }
    return v
}

private fun report(problems: List<String>, what: String, total: Int) {
    if (problems.isEmpty()) {
        println("$what: all $total match Scryfall on every requested field.")
    } else {
        println("$what: ${problems.size} discrepancy(ies) across $total")
        problems.forEach { println("  - $it") }
    }
}

private fun String.q(): String = "\"" + replace("\n", "\\n") + "\""

private fun Rarity.scryfall(): String = name.lowercase()

private fun CardDefinition.colorIdentityString(): String =
    colorIdentity.sortedBy { Color.entries.indexOf(it) }.joinToString(",") { it.symbol.toString() }

private fun JsonObject.colorIdentityString(): String {
    val arr = this["color_identity"] as? JsonArray ?: return ""
    return arr.map { (it as JsonPrimitive).content }
        .sortedBy { sym -> Color.entries.indexOfFirst { it.symbol == sym.firstOrNull() } }
        .joinToString(",")
}

private fun CardDefinition.scryfallId(): String? =
    metadata.scryfallId
        ?: metadata.imageUri?.let { Regex("""/([0-9a-f-]{36})\.""").find(it)?.groupValues?.get(1) }

private fun JsonObject.faces(): List<JsonObject> {
    val arr = this["card_faces"] as? JsonArray
    if (arr != null && arr.size >= 2) return arr.map { it.jsonObject }
    return listOf(this)
}

private fun JsonObject.imageNormal(): String? =
    (this["image_uris"] as? JsonObject)?.get("normal")?.let { (it as JsonPrimitive).content }

private fun JsonObject.str(key: String): String? =
    (this[key] as? JsonPrimitive)?.takeIf { !it.isString || it.content.isNotEmpty() }?.content

private fun dumpFile(): File {
    var dir: File? = File(System.getProperty("user.dir")).absoluteFile
    while (dir != null) {
        for (root in listOf("backlog", "backlog/archived")) {
            val f = File(dir, "$root/$DUMP_RELATIVE")
            if (f.exists()) return f
        }
        dir = dir.parentFile
    }
    error("Could not locate $DUMP_RELATIVE under backlog/ or backlog/archived/ " +
        "from ${System.getProperty("user.dir")}")
}
