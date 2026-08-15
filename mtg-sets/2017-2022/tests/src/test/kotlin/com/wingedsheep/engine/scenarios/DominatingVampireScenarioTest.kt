package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Dominating Vampire (VOW #154) — {1}{R}{R} Creature — Vampire, 3/3.
 *
 *   When this creature enters, gain control of target creature with mana value less than or equal
 *   to the number of Vampires you control until end of turn. Untap that creature. It gains haste
 *   until end of turn.
 *
 * A "Threaten" whose reach is gated on a dynamic mana-value cap (your Vampire count, evaluated at
 * target selection). Dominating Vampire counts itself, so a lone copy caps at mana value 1 — enough
 * to steal Savannah Lions ({W}, mana value 1). Covers the control change, the untap, and the haste
 * grant.
 */
class DominatingVampireScenarioTest : ScenarioTestBase() {

    init {
        context("Dominating Vampire") {

            test("ETB steals a mana-value-1 creature: control change + untap + haste") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dominating Vampire")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    // Opponent's tapped 2/1 — mana value 1, so a lone Dominating Vampire can steal it.
                    .withCardOnBattlefield(2, "Savannah Lions", tapped = true, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Dominating Vampire")
                withClue("Casting Dominating Vampire should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack() // enters → ETB trigger asks for a target

                val lions = game.findPermanent("Savannah Lions")!!
                withClue("The ETB trigger should pause for target selection") {
                    game.hasPendingDecision() shouldBe true
                }
                val select = game.selectTargets(listOf(lions))
                withClue("Targeting the mana-value-1 creature is legal: ${select.error}") {
                    select.error shouldBe null
                }
                game.resolveStack()

                withClue("Control of Savannah Lions passes to player 1") {
                    game.state.projectedState.getController(lions) shouldBe game.player1Id
                }
                withClue("The stolen creature is untapped") {
                    (game.state.getEntity(lions)?.has<TappedComponent>() ?: false) shouldBe false
                }
                withClue("The stolen creature gains haste") {
                    game.state.projectedState.hasKeyword(lions, Keyword.HASTE) shouldBe true
                }
            }
        }
    }
}
