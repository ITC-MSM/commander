package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.BrionStoutarm
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Brion Stoutarm (LRW #246) — "Lifelink. {R}, {T}, Sacrifice another creature: Brion Stoutarm
 * deals damage equal to the sacrificed creature's power to target player or planeswalker."
 *
 * The damage amount is read off the *sacrificed* creature (not Brion), the sacrifice must be
 * *another* creature, and because Brion is the damage source its lifelink applies to the ability's
 * damage too.
 */
class BrionStoutarmScenarioTest : ScenarioTestBase() {

    private val flingAbility = BrionStoutarm.activatedAbilities.single().id

    private fun TestGame.drain() {
        var guard = 0
        while (guard++ < 15) {
            when (val decision = getPendingDecision()) {
                is SelectCardsDecision -> selectCards(decision.options.take(decision.minSelections))
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                is YesNoDecision -> answerYesNo(true)
                null -> if (state.stack.isNotEmpty()) resolveStack() else break
                else -> error("unexpected decision $decision")
            }
        }
    }

    init {
        context("Brion Stoutarm") {

            test("throws the sacrificed creature's power at the opponent and gains that much life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brion Stoutarm", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant") // 3/3 — the fodder
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val brion = game.findPermanent("Brion Stoutarm")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = brion,
                        abilityId = flingAbility,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                game.drain()

                withClue("the 3/3 was sacrificed as a cost") {
                    game.isInGraveyard(1, "Hill Giant") shouldBe true
                }
                withClue("Brion tapped to pay") {
                    game.state.getEntity(brion)?.has<TappedComponent>() shouldBe true
                }
                withClue("damage equals the sacrificed creature's power (3), not Brion's (4)") {
                    game.getLifeTotal(2) shouldBe 17
                }
                withClue("Brion is the damage source, so its lifelink applies") {
                    game.getLifeTotal(1) shouldBe 23
                }
            }

            test("Brion can't sacrifice itself — with no other creature the ability can't be activated") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brion Stoutarm", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val brion = game.findPermanent("Brion Stoutarm")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = brion,
                        abilityId = flingAbility,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                )
                withClue("'sacrifice another creature' excludes Brion itself") {
                    result.error shouldNotBe null
                }
                game.findPermanent("Brion Stoutarm") shouldNotBe null
                game.getLifeTotal(2) shouldBe 20
            }
        }
    }
}
