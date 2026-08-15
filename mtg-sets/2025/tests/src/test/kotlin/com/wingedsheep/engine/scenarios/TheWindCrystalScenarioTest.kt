package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Wind Crystal (FIN #43).
 *
 * {2}{W}{W} Legendary Artifact
 *  - White spells you cast cost {1} less to cast.
 *  - If you would gain life, you gain twice that much life instead.
 *  - {4}{W}{W}, {T}: Creatures you control gain flying and lifelink until end of turn.
 *
 * Covers the life-gain doubling replacement and the group flying+lifelink grant. The {1}-less
 * cost reduction reuses the existing ModifySpellCost primitive so it isn't re-exercised here.
 */
class TheWindCrystalScenarioTest : ScenarioTestBase() {

    init {
        context("The Wind Crystal") {

            test("doubles life you gain (Reviving Dose gains 6 instead of 3)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Wind Crystal")
                    .withCardInHand(1, "Reviving Dose")
                    .withLandsOnBattlefield(1, "Plains", 3) // pays {1}{W}
                    .withLifeTotal(1, 20)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Reviving Dose").error shouldBe null
                game.resolveStack()

                withClue("Reviving Dose gains 3, doubled to 6 by The Wind Crystal") {
                    game.getLifeTotal(1) shouldBe 26 // 20 + (3 * 2)
                }
            }

            test("{4}{W}{W}, {T}: creatures you control gain flying and lifelink") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Wind Crystal", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6) // pays {4}{W}{W}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crystal = game.findPermanent("The Wind Crystal")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val abilityId = cardRegistry.getCard("The Wind Crystal")!!
                    .script.activatedAbilities[0].id

                withClue("Grizzly Bears has neither keyword before activation") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe false
                    game.state.projectedState.hasKeyword(bears, Keyword.LIFELINK) shouldBe false
                }

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = crystal,
                        abilityId = abilityId,
                    )
                )
                withClue("Activating The Wind Crystal should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Grizzly Bears gains flying and lifelink until end of turn") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe true
                    game.state.projectedState.hasKeyword(bears, Keyword.LIFELINK) shouldBe true
                }
            }
        }
    }
}
