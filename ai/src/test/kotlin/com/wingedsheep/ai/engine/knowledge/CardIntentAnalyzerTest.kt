package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.dsk.cards.UnholyAnnexRitualChamber
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe

/**
 * What the structural analyzer makes of real cards.
 *
 * These are the shapes the three Phase 6 consumers depend on, pinned against the actual card
 * definitions rather than hand-built scripts — a `cardDef` that changes shape (an effect renamed,
 * a static ability restructured) should fail here, which is the point.
 */
class CardIntentAnalyzerTest : ScenarioTestBase() {

    private fun intentOf(name: String) = CardIntentAnalyzer.analyze(cardRegistry.requireCard(name))

    private fun roomFace(name: String) = UnholyAnnexRitualChamber.cardFaces.first { it.name == name }

    init {
        test("a repeatable tapper reads as a repeatable tapper") {
            val intent = intentOf("Icy Manipulator")
            intent.tags shouldContain IntentTag.TAPPER
            intent.repeatable shouldBe true
            intent.speed shouldBe Speed.ACTIVATED
            intent.affectsOpponent shouldBe true
            withClue("must clear the ~2.7 a Disenchant costs in card advantage") {
                intent.staticPriorValue shouldBeGreaterThan 2.7
            }
        }

        test("a mana rock is ramp, and is worth about a land") {
            val intent = intentOf("Mind Stone")
            intent.tags shouldContain IntentTag.RAMP
            withClue("the draw ability sacrifices the rock, so it is not a repeatable engine") {
                intent.repeatable shouldBe false
            }
            intent.staticPriorValue shouldBeLessThan 1.0
        }

        test("an anthem carries its per-creature bonus separately from its prior") {
            val intent = intentOf("Glorious Anthem")
            intent.tags shouldContain IntentTag.ANTHEM
            intent.speed shouldBe Speed.STATIC
            withClue("+1/+1 to each creature = 2 points of stats") { intent.anthemBonus shouldBe 2 }
            intent.staticPriorValue shouldBeGreaterThan 2.7
        }

        test("an ETB exile-until-leaves enchantment is exile removal, but not a repeatable one") {
            val intent = intentOf("Banishing Light")
            intent.tags shouldContain IntentTag.REMOVAL
            intent.tags shouldContain IntentTag.EXILE_REMOVAL
            withClue("its own ETB trigger fires once per object, not once per turn") {
                intent.repeatable shouldBe false
            }
            intent.staticPriorValue shouldBeGreaterThan 2.7
        }

        test("an instant that destroys is instant-speed removal, and not a combat trick") {
            val intent = intentOf("Disenchant")
            intent.tags shouldContain IntentTag.REMOVAL
            intent.speed shouldBe Speed.INSTANT
            intent.tags shouldNotContain IntentTag.COMBAT_TRICK
            withClue("a non-permanent is never on a battlefield, so it has no board prior") {
                intent.staticPriorValue shouldBe 0.0
            }
        }

        test("an instant pump is a combat trick") {
            val intent = intentOf("Giant Growth")
            intent.tags shouldContain IntentTag.PUMP
            intent.tags shouldContain IntentTag.COMBAT_TRICK
            intent.speed shouldBe Speed.INSTANT
        }

        test("a sorcery pump is not a combat trick") {
            val intent = intentOf("Titanic Growth")
            intent.speed shouldBe Speed.INSTANT
        }

        test("a sweeper is tagged as one, and reads as opponent-facing") {
            val intent = intentOf("Wrath of God")
            intent.tags shouldContain IntentTag.REMOVAL
            intent.tags shouldContain IntentTag.SWEEPER
            intent.affectsOpponent shouldBe true
        }

        test("burn carries its reach") {
            intentOf("Lightning Bolt").removalReach shouldBe 3
        }

        test("a vanilla creature is uninterpretable, and keeps the historical flat prior") {
            val intent = intentOf("Grizzly Bears")
            intent.tags shouldBe emptySet()
            intent.staticPriorValue shouldBe CardIntent.UNKNOWN.staticPriorValue
        }

        test("a Room's rules text lives on its faces, and the analyzer reads it") {
            // Top-level script is empty for a split card (CR 709) — reading only that priced this
            // repeatable draw engine as a vanilla enchantment, and the AI never cast it.
            val intent = CardIntentAnalyzer.analyze(UnholyAnnexRitualChamber)
            intent.tags shouldContain IntentTag.DRAW
            intent.tags shouldContain IntentTag.TOKEN_MAKER
            intent.repeatable shouldBe true
            intent.staticPriorValue shouldBeGreaterThan 2.7
        }

        test("each Room half is valued on its own, so unlocking a door is worth something") {
            val annex = CardIntentAnalyzer.analyzeFace(UnholyAnnexRitualChamber, roomFace("Unholy Annex"))
            withClue("an end-step draw trigger that fires every turn") {
                annex.tags shouldContain IntentTag.DRAW
                annex.repeatable shouldBe true
                annex.staticPriorValue shouldBeGreaterThan 2.7
            }

            val chamber = CardIntentAnalyzer.analyzeFace(UnholyAnnexRitualChamber, roomFace("Ritual Chamber"))
            chamber.tags shouldContain IntentTag.TOKEN_MAKER
            withClue("'when you unlock this door' fires once for that door, like an ETB") {
                chamber.repeatable shouldBe false
                chamber.staticPriorValue shouldBe CardIntent.UNKNOWN.staticPriorValue
            }
        }

        test("the catalog resolves a face by name, and shrugs at a face that isn't there") {
            val catalog = IntentCatalog.of(cardRegistry.apply { register(UnholyAnnexRitualChamber) })
            catalog.forFace(UnholyAnnexRitualChamber.name, "Unholy Annex")!!.tags shouldContain IntentTag.DRAW
            catalog.forFace(UnholyAnnexRitualChamber.name, "Broom Closet") shouldBe null
            IntentCatalog.NONE.forFace(UnholyAnnexRitualChamber.name, "Unholy Annex") shouldBe null
        }

        test("the catalog is off by default and answers nothing") {
            IntentCatalog.NONE.isEnabled shouldBe false
            IntentCatalog.NONE.forName("Icy Manipulator") shouldBe null
        }

        test("an enabled catalog resolves by name and shrugs at a name it does not know") {
            val catalog = IntentCatalog.of(cardRegistry)
            catalog.isEnabled shouldBe true
            catalog.forName("Icy Manipulator")?.tags shouldBe intentOf("Icy Manipulator").tags
            catalog.forName("Definitely Not A Card") shouldBe null
        }
    }
}
