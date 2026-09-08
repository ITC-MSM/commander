package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Nova Chaser (LRW #187) — trample, champion an Elemental.
 *
 * The tribal champion quality is the point here: "an Elemental" is a bare tribal noun, so per
 * CR 109.2 it means an Elemental *permanent*, not just an Elemental creature — and a creature of
 * some other type is not a legal choice at all.
 */
class NovaChaserScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Nova Chaser — champion an Elemental") {

            test("only Elementals you control are offered — a Bear is not") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Nova Chaser")
                    .withCardOnBattlefield(1, "Smokebraider")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Nova Chaser").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                decision.options shouldContain game.findPermanent("Smokebraider")!!
                withClue("Grizzly Bears is a Bear, not an Elemental") {
                    decision.options shouldNotContain game.findPermanent("Grizzly Bears")!!
                }
            }

            test("exiling the Elemental keeps the Chaser, and it returns when the Chaser dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Nova Chaser")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Smokebraider")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val smokebraider = game.findPermanent("Smokebraider")!!
                game.castSpell(1, "Nova Chaser").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(smokebraider)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Nova Chaser") shouldBe true
                game.isInExile(1, "Smokebraider") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Nova Chaser")!!).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Smokebraider") shouldBe true
            }

            test("with no other Elemental the Chaser is sacrificed") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Nova Chaser")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Nova Chaser").error shouldBe null
                game.resolveStack()

                game.hasPendingDecision() shouldBe false
                game.isInGraveyard(1, "Nova Chaser") shouldBe true
                withClue("the Bear was never a legal choice, so it is untouched") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("it is a 10/2 with trample") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Nova Chaser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chaser = game.findPermanent("Nova Chaser")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(chaser, Keyword.TRAMPLE) shouldBe true
                projected.getPower(chaser) shouldBe 10
                projected.getToughness(chaser) shouldBe 2
            }
        }
    }
}
