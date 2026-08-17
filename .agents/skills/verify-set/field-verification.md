# Field verification: the harness and what it finds

Stage 2 of [`SKILL.md`](SKILL.md). Read this before writing the test.

The test compares the **compiled `CardDefinition`s the engine actually loads** — not the DSL source, not
the backlog — against a committed Scryfall dump, on thirteen fields:

```
name  mana_cost  color_identity  type_line  oracle_text  power  toughness  loyalty
rarity  collector_number  artist  flavor_text  image_uris
```

It collects every discrepancy and asserts the list is empty at the end, so one run yields the whole
worklist. That property is the point — don't convert it to a fail-fast.

---

## The template

`mtg-sets/src/test/kotlin/com/wingedsheep/mtg/sets/<Xxx>CardFieldVerificationTest.kt`. Substitute the set
code, the class name, and the dump slug. 

It belongs in the aggregator module `mtg-sets`, **not** in an era module or its `tests` child — that's
where the whole-corpus tests live (`CardDefinitionSnapshotTest`, `CardLintTest`), where `MtgSetCatalog`
can see every set, and where `kotlinx-serialization-json` is already on the test classpath.

Delete it when finished running the validation.

```kotlin
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

private const val SET_CODE = "MSH"
private const val DUMP_RELATIVE = "sets/marvel-super-heroes/msh_set.json"

/**
 * Field-level verification of every registered Marvel Super Heroes card against authoritative
 * Scryfall data.
 *
 * Checks the **compiled [CardDefinition]s** the engine loads — the real source of truth for play —
 * against the committed Scryfall dump, field by field: name, mana_cost, color_identity, type_line,
 * oracle_text, power, toughness, loyalty, rarity, collector_number, artist, flavor_text, image_uris.
 *
 * Double-faced cards are checked **face by face**: our front face against Scryfall `card_faces[0]`
 * and [CardDefinition.backFace] against `card_faces[1]` for the per-face fields. color_identity,
 * rarity and collector_number are whole-card and come from the top-level object.
 *
 * Reprint rows ([com.wingedsheep.sdk.model.MtgSet.printings]) and basic lands are outside
 * `set.cards`, so they get their own passes below.
 */
class MshCardFieldVerificationTest : FunSpec({

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

            // Whole-card fields — never per-face on Scryfall.
            check(problems, card.name, "color_identity", card.colorIdentityString(), a.colorIdentityString())
            check(problems, card.name, "rarity", card.metadata.rarity.scryfall(), a.str("rarity"))
            check(problems, card.name, "collector_number", cn ?: "", a.str("collector_number"))

            // Per-face fields: pair our faces with Scryfall's card_faces, or the whole object when
            // the card is single-faced.
            val ourFaces = if (card.isDoubleFaced) listOf(card, card.backFace!!) else listOf(card)
            val authFaces = a.faces()
            if (ourFaces.size != authFaces.size) {
                problems += "${card.name}: face-count mismatch ours=${ourFaces.size} auth=${authFaces.size}"
                continue
            }
            for ((idx, pair) in ourFaces.zip(authFaces).withIndex()) {
                val (ourFace, authFace) = pair
                val label = if (card.isDoubleFaced) "${card.name}[$idx:${ourFace.name}]" else card.name
                // The back face of a *nonmodal* DFC (CR 712.2) is reached only by transforming, and
                // has no mana symbols where a mana cost would go — it has no mana cost (CR 202.1b),
                // its colors coming from a color indicator instead (CR 204). Scryfall still lists a
                // printed mana_cost for that face. Don't flag it. A *modal* DFC back face has a real
                // mana cost and no indicator, so this correctly leaves it checked.
                val nonmodalBack = card.isDoubleFaced && idx == 1 && ourFace.colorIndicator != null
                checkFace(problems, label, ourFace, authFace, skipManaCost = nonmodalBack)
            }
        }

        report(problems, "$SET_CODE cards", cards.size)
        problems shouldBe emptyList()
    }

    // Reprint / variant rows carry their own per-printing metadata and are absent from set.cards.
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

    // Basic lands are also outside set.cards. Art *ordering* is BasicLandArtOrderTest's job; this
    // only asserts each declared land is a real printing in the set at the collector number we claim.
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

/**
 * Known SDK-level *rendering* gaps, waived and tracked for follow-up: the card is right, the compiled
 * rendering isn't. Keyed to the exact (label, field) pair so any **new** discrepancy still fails.
 * Every entry needs a reason in the commit message and a tracked follow-up. A waiver on power,
 * toughness, type_line or color_identity is almost certainly a real bug — don't add one.
 */
private val WAIVED: Set<Pair<String, String>> = emptySet()

/** Compare every per-face field of one of our faces against its Scryfall face object. */
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
    // Compare image URIs without Scryfall's volatile `?<timestamp>` cache-buster — the image identity
    // is the path (…/<scryfall-id>.jpg); the query bumps on every re-host.
    check(problems, label, "image_uris",
        def.metadata.imageUri?.substringBefore("?"), a.imageNormal()?.substringBefore("?"))
}

/** A field matches when the two normalized values are equal, treating null/blank as the empty string. */
private fun check(problems: MutableList<String>, label: String, field: String, ours: String?, auth: String?) {
    if ((label to field) in WAIVED) return
    val o = canon(field, ours)
    val a = canon(field, auth)
    if (o != a) problems += "$label.$field: ours=${o.q()} auth=${a.q()}"
}

/**
 * Normalize for comparison. Beyond trim and null-as-empty: a dynamic P/T with a fixed offset renders
 * as `*+N` from [com.wingedsheep.sdk.model.CharacteristicValue], while Scryfall writes `N+*`. Same
 * value, different word order — canonicalize rather than editing the card.
 */
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

/** Our color identity in Scryfall's canonical WUBRG order, e.g. {W}{U} -> "W,U". */
private fun CardDefinition.colorIdentityString(): String =
    colorIdentity.sortedBy { Color.entries.indexOf(it) }.joinToString(",") { it.symbol.toString() }

private fun JsonObject.colorIdentityString(): String {
    val arr = this["color_identity"] as? JsonArray ?: return ""
    return arr.map { (it as JsonPrimitive).content }
        .sortedBy { sym -> Color.entries.indexOfFirst { it.symbol == sym.firstOrNull() } }
        .joinToString(",")
}

/** Prefer the recorded Scryfall id; fall back to the one embedded in the image URI path. */
private fun CardDefinition.scryfallId(): String? =
    metadata.scryfallId
        ?: metadata.imageUri?.let { Regex("""/([0-9a-f-]{36})\.""").find(it)?.groupValues?.get(1) }

/** Scryfall's per-face objects, or the whole object when the card is single-faced. */
private fun JsonObject.faces(): List<JsonObject> {
    val arr = this["card_faces"] as? JsonArray
    if (arr != null && arr.size >= 2) return arr.map { it.jsonObject }
    return listOf(this)
}

private fun JsonObject.imageNormal(): String? =
    (this["image_uris"] as? JsonObject)?.get("normal")?.let { (it as JsonPrimitive).content }

private fun JsonObject.str(key: String): String? =
    (this[key] as? JsonPrimitive)?.takeIf { !it.isString || it.content.isNotEmpty() }?.content

/**
 * Walk up from the working directory to the dump. Checks the archived root too, so the test keeps
 * working after Stage 4 moves the backlog directory under `backlog/archived/`.
 */
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
```

Run it alone while iterating — it's seconds against a warm daemon:

```bash
just test-class MshCardFieldVerificationTest
```

---

## The taxonomy: what these runs actually found

Ordered by how much it matters. **Gameplay** classes change how the card plays and are the reason this
stage exists. **Display** classes are cosmetic but land in the client and the snapshot. **Harness**
classes are false positives — fixing the card there makes the card wrong.

| # | Class | Kind | Signature | Do |
|---|---|---|---|---|
| 1 | Wrong power/toughness | gameplay | `power: ours="4" auth="5"` | Fix the card. TMT's Leatherhead, Swamp Stalker shipped a 4/5 as a 4/4 behind a green backlog. |
| 2 | Wrong color identity | gameplay | `color_identity: ours="G" auth="G,U"` | Set `colorIdentity = "GU"` explicitly. The heuristic derives identity from the mana cost, so it misses symbols in *activated-ability* costs (TMT Venus's pay-`{U}`) and on a **DFC back face** (TLA Avatar Aang, RGWU → WUBRG). Both were real. |
| 3 | Missing supertype / wrong type line | gameplay | `type_line: ours="Creature — …" auth="Legendary Creature — …"` | Fix the type line. Legendary is load-bearing (the "legend rule", CR 704.5j). |
| 4 | Reminder text inlined that Oracle doesn't have | display | `oracle_text` ours carries `(Whenever this creature attacks, add {R}{R}…)` | Strip it. Scryfall's `oracle_text` includes reminder text only where the printed card does — a bare `Firebending 2` on the card stays bare. Hit TLA's Bumi/Iroh/Ozai and TMT's Leonardo (Sneak). |
| 5 | Reminder text missing that Oracle *does* have | display | inverse of 4 | Add it verbatim. TLA's Saber-Tooth Moose-Lion (forestcycling), TMT's Equip / Scry / Menace. |
| 6 | Missing flavor text | display | `flavor_text: ours="" auth="…"` | Add it. Nine on TLA alone — the single highest-count line item. |
| 7 | Mis-quoted flavor text | display | spurious wrapping `"…"`, or wrong attribution dash | Match Scryfall byte for byte, including `\n—Speaker`. TLA's Turtle-Duck had invented outer quotes. |
| 8 | Wrong artist | display | `artist: ours="X" auth="Y"` | Fix. Three on TMT. Easy to get wrong when a card was drafted from a sibling printing. |
| 9 | Line breaks | display | text equal but for `\n` placement | Match Scryfall's breaks exactly — TMT's Class level lines. `\n` vs joined-with-space is a real diff in the snapshot. |
| 10 | Dynamic P/T word order | **harness** | `power: ours="*+1" auth="1+*"` | Leave the card alone; `canon()` normalizes it. `CharacteristicValue` renders offset-last. |
| 11 | Image cache-buster / back-face mana cost | **harness** | `?1764121885` differing; a mana cost on a transformed back face | Leave the card alone; the template strips the query and skips the nonmodal back face (CR 202.1b / 204). |

Two notes on citing rules here, since the prior harnesses got one wrong:

- Double-faced cards are **CR 712**, not 711 — 711 is Leveler Cards. `SpmCardFieldVerificationTest`'s
  comment says 711; it's stale. Nonmodal (transforming) DFCs are 712.2, modal DFCs 712.3.
- The back face having no mana cost is **CR 202.1b** (objects with no mana symbols where the cost would
  appear have no mana cost), with its colors coming from the color indicator, **CR 204**. CR 202.3a is the
  related mana-*value* carve-out.

Verified against `MagicCompRules_20260808.txt`. Re-check if a newer dated file appears in the repo root.

---

## The live cross-check (optional)

The Kotlin test proves *compiled cards == dump*. A python script proves *dump == live Scryfall*, which
matters once — when you first commit the dump — and then only if you suspect the dump is stale. All five
branches shipped one (`verify_<code>_set.py`, ~80 lines, next to the dump). Its extra value over the
Kotlin test is the **presence** assertion: for each required field it checks the field is either present
in the dump *or* legitimately empty on **both** sides, which catches "Scryfall has a flavor text we never
recorded" as distinct from "neither has one".

Write it only if you want that property re-runnable. Otherwise a one-off diff of the fresh fetch against
the committed dump is the same evidence, and `SKILL.md` Stage 0's fetch is already that.

