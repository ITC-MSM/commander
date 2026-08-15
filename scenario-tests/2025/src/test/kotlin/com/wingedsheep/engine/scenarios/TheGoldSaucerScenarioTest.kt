package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Gold Saucer (FIN #279) — Land — Town.
 *
 *  - {T}: Add {C}.
 *  - {2}, {T}: Flip a coin. If you win the flip, create a Treasure token.
 *  - {3}, {T}, Sacrifice two artifacts: Draw a card.
 *
 * All three abilities are built from existing SDK primitives (AddColorlessMana, FlipCoinEffect,
 * SacrificeMultiple cost + DrawCards), so these tests exercise the wiring end-to-end. The coin
 * flip is random, so its test only asserts that the ability activates and resolves cleanly.
 */
class TheGoldSaucerScenarioTest : ScenarioTestBase() {

    init {
        context("The Gold Saucer") {

            test("{T}: Add {C} fills the mana pool with one colorless mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Gold Saucer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val saucer = game.findPermanent("The Gold Saucer")!!
                val manaAbility = cardRegistry.getCard("The Gold Saucer")!!
                    .activatedAbilities[0].id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = saucer,
                        abilityId = manaAbility,
                    )
                )
                withClue("Activating the mana ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("The mana ability adds one colorless mana") {
                    pool?.colorless shouldBe 1
                }
            }

            test("{2}, {T}: Flip a coin activates and resolves") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Gold Saucer", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Wastes", 2) // pays {2}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val saucer = game.findPermanent("The Gold Saucer")!!
                val flipAbility = cardRegistry.getCard("The Gold Saucer")!!
                    .activatedAbilities[1].id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = saucer,
                        abilityId = flipAbility,
                    )
                )
                withClue("Activating the coin-flip ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                // The flip is random; just assert it resolves cleanly off the stack. A Treasure
                // is created on a win and not on a loss, so we only check 0..1 Treasures exist.
                game.resolveStack()
                withClue("At most one Treasure is created (depending on the flip)") {
                    (game.findAllPermanents("Treasure").size in 0..1) shouldBe true
                }
            }

            test("{3}, {T}, Sacrifice two artifacts: Draw a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Gold Saucer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Ornithopter", summoningSickness = false)
                    .withCardOnBattlefield(1, "Ornithopter", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Wastes", 3) // pays {3}
                    .withCardInLibrary(1, "Grizzly Bears") // a card to draw
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val saucer = game.findPermanent("The Gold Saucer")!!
                val artifacts = game.findAllPermanents("Ornithopter")
                withClue("Two artifacts are on the battlefield to sacrifice") {
                    artifacts.size shouldBe 2
                }
                val drawAbility = cardRegistry.getCard("The Gold Saucer")!!
                    .activatedAbilities[2].id

                val handBefore = game.state.getHand(game.player1Id).size

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = saucer,
                        abilityId = drawAbility,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = artifacts),
                    )
                )
                withClue("Activating the draw ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                withClue("Both artifacts were sacrificed as a cost") {
                    game.findAllPermanents("Ornithopter").size shouldBe 0
                }
                game.resolveStack()

                withClue("The ability draws exactly one card") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore + 1
                }
            }
        }
    }
}
