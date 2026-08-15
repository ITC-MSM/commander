package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Dreadmaw's Ire (LCI #147) — {R} Instant, Uncommon.
 *
 * "Until end of turn, target attacking creature gets +2/+2 and gains trample and 'Whenever this
 *  creature deals combat damage to a player, destroy target artifact that player controls.'"
 *
 * The granted "…destroy target artifact **that player** controls" clause exercises the
 * [com.wingedsheep.sdk.scripting.predicates.ControllerPredicate.ControlledByTriggeringPlayer]
 * predicate: the target must be an artifact controlled by the *damaged* player. This pins that
 * (a) the SELF-binding combat-damage-to-player trigger fires from a *granted* ability, (b) its
 * target resolves to the damaged player's artifact — not the caster's, and not any artifact — and
 * (c) with no legal target it fizzles cleanly (CR 603.3d).
 */
class DreadmawsIreScenarioTest : ScenarioTestBase() {

    init {
        context("Dreadmaw's Ire") {

            test("+2/+2, trample, and the granted trigger destroys an artifact that player controls") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")   // the attacker (2/2 -> 4/4)
                    .withCardOnBattlefield(1, "Bonesplitter")    // caster's artifact — must survive
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Dreadmaw's Ire")
                    .withCardOnBattlefield(2, "Sol Ring")        // the damaged player's artifact — destroyed
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val enemyRock = game.findPermanent("Sol Ring")!!    // only player 2 has one
                val casterRock = game.findPermanent("Bonesplitter")!!

                // Attack with the bears, then buff it during the declare-attackers priority window.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                withClue("Grizzly Bears should be able to attack") {
                    game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                }

                val cast = game.castSpell(1, "Dreadmaw's Ire", bears)
                withClue("Dreadmaw's Ire should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("+2/+2 applies") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 4
                }
                withClue("trample is granted") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
                }

                // Advance into the combat-damage step: the 4 combat damage to player 2 fires the
                // granted trigger, which asks for its artifact target.
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                var guard = 0
                while (game.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
                    game.resolveStack(); guard++
                }
                val td = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected ChooseTargetsDecision for the granted destroy trigger; got ${game.state.pendingDecision}")

                withClue("only the damaged player's artifact is a legal target — not the caster's") {
                    td.legalTargets[0] shouldBe listOf(enemyRock)
                }

                game.submitDecision(TargetsResponse(td.id, mapOf(0 to listOf(enemyRock))))
                game.resolveStack()

                withClue("the damaged player's Sol Ring is destroyed") {
                    game.isInGraveyard(2, "Sol Ring") shouldBe true
                }
                withClue("the caster's own Bonesplitter is untouched") {
                    game.state.getBattlefield().contains(casterRock) shouldBe true
                }
            }

            test("granted trigger fizzles cleanly when the damaged player controls no artifact") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Dreadmaw's Ire")
                    // Player 2 controls NO artifact.
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                val cast = game.castSpell(1, "Dreadmaw's Ire", bears)
                withClue("Dreadmaw's Ire should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val lifeBefore = game.getLifeTotal(2)

                // With no legal artifact target the mandatory-target trigger fizzles (CR 603.3d) —
                // no ChooseTargetsDecision is ever raised and the game proceeds normally.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("no target decision was raised for the fizzled trigger") {
                    (game.state.pendingDecision is ChooseTargetsDecision) shouldBe false
                }
                withClue("the 4 combat damage still went through") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 4
                }
            }
        }
    }
}
