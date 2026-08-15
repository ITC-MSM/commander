package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Alchemist's Assistant (TDM #71) — {1}{B} Monkey, 2/1, Lifelink.
 *
 * "Renew — {1}{B}, Exile this card from your graveyard: Put a lifelink counter on target
 *  creature. Activate only as a sorcery."
 *
 * The lifelink counter grants the Lifelink keyword via projected state (KEYWORD_COUNTER_MAP),
 * and the card is exiled from the graveyard as part of the cost.
 */
class AlchemistsAssistantScenarioTest : ScenarioTestBase() {

    private val renewAbilityId =
        cardRegistry.getCard("Alchemist's Assistant")!!.activatedAbilities.first().id

    init {
        context("Alchemist's Assistant renew") {

            test("puts a lifelink counter (granting Lifelink) on a target creature and exiles itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Alchemist's Assistant")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2, no lifelink
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val assistant = game.findCardsInGraveyard(1, "Alchemist's Assistant").first()
                val creature = game.findPermanent("Glory Seeker")!!

                withClue("Glory Seeker has no Lifelink before the counter is placed") {
                    game.state.projectedState.hasKeyword(creature, Keyword.LIFELINK) shouldBe false
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = assistant,
                        abilityId = renewAbilityId,
                        targets = listOf(ChosenTarget.Permanent(creature)),
                    )
                )
                withClue("Activating renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val counters = game.state.getEntity(creature)?.get<CountersComponent>()
                withClue("Glory Seeker gets one lifelink counter") {
                    (counters?.counters?.get(CounterType.LIFELINK) ?: 0) shouldBe 1
                }
                withClue("The lifelink counter grants the Lifelink keyword via projection") {
                    game.state.projectedState.hasKeyword(creature, Keyword.LIFELINK) shouldBe true
                }
                withClue("Alchemist's Assistant is exiled from the graveyard as part of the cost") {
                    game.findCardsInGraveyard(1, "Alchemist's Assistant").size shouldBe 0
                    game.state.getExile(game.player1Id).contains(assistant) shouldBe true
                }
            }
        }
    }
}
