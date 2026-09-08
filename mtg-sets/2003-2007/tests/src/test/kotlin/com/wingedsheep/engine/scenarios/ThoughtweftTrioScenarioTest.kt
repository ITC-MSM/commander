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
 * Thoughtweft Trio (LRW #44) — first strike, vigilance, champion a Kithkin, and "this creature can
 * block any number of creatures."
 *
 * The champion keyword's matrix lives in `ChampionKeywordTest`; this pins the Kithkin quality and
 * the multi-block static, which is the ability a normal blocking-legality check would refuse.
 */
class ThoughtweftTrioScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Thoughtweft Trio — champion a Kithkin") {

            test("only Kithkin you control are offered") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Thoughtweft Trio")
                    .withCardOnBattlefield(1, "Kinsbaile Balloonist")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Thoughtweft Trio").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                decision.options shouldContain game.findPermanent("Kinsbaile Balloonist")!!
                withClue("a Bear is not a Kithkin") {
                    decision.options shouldNotContain game.findPermanent("Grizzly Bears")!!
                }
            }

            test("exiling the Kithkin keeps the Trio, and it returns when the Trio dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Thoughtweft Trio")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Kinsbaile Balloonist")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kithkin = game.findPermanent("Kinsbaile Balloonist")!!
                game.castSpell(1, "Thoughtweft Trio").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(kithkin)).error shouldBe null
                game.resolveStack()
                game.isInExile(1, "Kinsbaile Balloonist") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Thoughtweft Trio")!!).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Kinsbaile Balloonist") shouldBe true
            }
        }

        context("Thoughtweft Trio — can block any number of creatures") {

            test("it blocks two attackers at once and kills both with first strike") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Thoughtweft Trio", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Savannah Lions", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 1, "Savannah Lions" to 1))
                    .error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(
                    mapOf("Thoughtweft Trio" to listOf("Grizzly Bears", "Savannah Lions"))
                ).error shouldBe null
                game.resolveStack()
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)

                withClue("a 5/5 first striker splitting 5 damage kills a 2/2 and a 2/1") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isOnBattlefield("Savannah Lions") shouldBe false
                }
                withClue("first strike means neither attacker ever dealt its damage back") {
                    game.isOnBattlefield("Thoughtweft Trio") shouldBe true
                }
            }

            test("it is a 5/5 with first strike and vigilance") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Thoughtweft Trio")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val trio = game.findPermanent("Thoughtweft Trio")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(trio, Keyword.FIRST_STRIKE) shouldBe true
                projected.hasKeyword(trio, Keyword.VIGILANCE) shouldBe true
                projected.getPower(trio) shouldBe 5
                projected.getToughness(trio) shouldBe 5
            }
        }
    }
}
