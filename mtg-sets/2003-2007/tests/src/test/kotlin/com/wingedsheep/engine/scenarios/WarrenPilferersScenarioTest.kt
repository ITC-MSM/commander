package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Warren Pilferers (LRW #146, {4}{B}, Creature — Goblin Rogue 3/3).
 *
 *   When this creature enters, return target creature card from your graveyard to your hand.
 *   If that card is a Goblin card, this creature gains haste until end of turn.
 *
 * The rider reads the *target*, not the Pilferers, and it is checked while the card is still in the
 * graveyard — so the tests prove both branches of that gate plus the graveyard-ownership restriction.
 */
class WarrenPilferersScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Warren Pilferers") {

            test("returning a Goblin card grants the Pilferers haste") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Warren Pilferers")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInGraveyard(1, "Boggart Forager")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Warren Pilferers").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("The enters trigger should ask for a graveyard target; got $decision") {
                    decision.shouldBeInstanceOf<ChooseTargetsDecision>()
                }
                val forager = game.findCardsInGraveyard(1, "Boggart Forager").single()
                game.selectTargets(listOf(forager))
                game.resolveStack()

                withClue("Boggart Forager should be back in Alice's hand") {
                    game.isInHand(1, "Boggart Forager") shouldBe true
                    game.isInGraveyard(1, "Boggart Forager") shouldBe false
                }
                withClue("A Goblin card was returned, so the Pilferers gains haste") {
                    val pilferers = game.findPermanent("Warren Pilferers")!!
                    stateProjector.project(game.state).hasKeyword(pilferers, Keyword.HASTE) shouldBe true
                }
            }

            test("returning a non-Goblin creature card returns it but grants no haste") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Warren Pilferers")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Warren Pilferers").error shouldBe null
                game.resolveStack()

                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("The return happens on both branches of the gate") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("Grizzly Bears is no Goblin, so no haste") {
                    val pilferers = game.findPermanent("Warren Pilferers")!!
                    stateProjector.project(game.state).hasKeyword(pilferers, Keyword.HASTE) shouldBe false
                }
            }

            test("an opponent's graveyard is never a legal source") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Warren Pilferers")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(2, "Boggart Forager")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Warren Pilferers").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<ChooseTargetsDecision>()
                val opponentsForager = game.findCardsInGraveyard(2, "Boggart Forager").single()
                withClue("Bob's Boggart Forager is not a legal target for Alice's Pilferers") {
                    decision.legalTargets[0].orEmpty() shouldNotContain opponentsForager
                }
            }
        }
    }
}
