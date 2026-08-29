package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Glarewielder (LRW #171) — {4}{R} Creature — Elemental Shaman 3/1
 *
 *   Haste
 *   When this creature enters, up to two target creatures can't block this turn.
 *   Evoke {1}{R}
 *
 * "Up to two" is one requirement of `count = 2, optional = true`, and the restriction is applied
 * once *per chosen target* — the effect is a `ForEachTargetEffect` over `CantBlock`, not a single
 * `CantBlock` that would silently only reach the first creature. Evoke is what the card is played
 * for: {1}{R}, the trigger still resolves, and the body is sacrificed on the way in.
 */
class GlarewielderScenarioTest : ScenarioTestBase() {

    init {
        context("Glarewielder") {

            test("both chosen creatures are barred from blocking — an untargeted one is not") {
                val game = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardInHand(1, "Glarewielder")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withCardOnBattlefield(2, "Force of Nature", summoningSickness = false)
                    // The control: never targeted, so it must still be able to block. Without it
                    // a rejected block is indistinguishable from a rejected *step*.
                    .withCardOnBattlefield(2, "Llanowar Elves", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val force = game.findPermanent("Force of Nature")!!

                game.castSpell(1, "Glarewielder").error shouldBe null
                game.resolveStack()

                withClue("the enters trigger asks for its up-to-two targets") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(courser, force)).error shouldBe null
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Glarewielder" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("the first target can't be declared as a blocker") {
                    game.declareBlockers(mapOf("Centaur Courser" to listOf("Glarewielder")))
                        .error shouldNotBe null
                }
                withClue("nor can the second — the restriction reached both targets, not just one") {
                    game.declareBlockers(mapOf("Force of Nature" to listOf("Glarewielder")))
                        .error shouldNotBe null
                }
                withClue("the untargeted creature blocks normally, so the step is not what refused") {
                    game.declareBlockers(mapOf("Llanowar Elves" to listOf("Glarewielder")))
                        .error shouldBe null
                }
            }

            test("evoked for {1}{R} it still fires its trigger and is then sacrificed") {
                val game = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardInHand(1, "Glarewielder")
                    // Exactly the evoke cost. {4}{R} is unpayable here, so a successful cast can
                    // only have been the alternative cost.
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!

                game.castSpellWithAlternativeCost(1, "Glarewielder").error shouldBe null
                game.resolveStack()

                withClue("the enters trigger still happens on an evoked cast") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(courser)).error shouldBe null
                game.resolveStack()

                withClue("evoke sacrificed the body once it had entered") {
                    game.isOnBattlefield("Glarewielder") shouldBe false
                    game.isInGraveyard(1, "Glarewielder") shouldBe true
                }
            }
        }
    }
}
