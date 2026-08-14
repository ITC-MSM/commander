package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.avr.cards.MoorlandInquisitor
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class MoorlandInquisitorScenarioTest : ScenarioTestBase() {
    init {
        val activateAbilityId = MoorlandInquisitor.activatedAbilities.first().id

        test("activated ability grants first strike until end of turn") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Moorland Inquisitor")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val inquisitor = game.findPermanent("Moorland Inquisitor")!!
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = inquisitor,
                    abilityId = activateAbilityId,
                ),
            )
            withClue("${result.error}") { result.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) {
                game.submitManaSourcesAutoPay()
            }
            game.resolveStack()

            game.state.projectedState.hasKeyword(inquisitor, Keyword.FIRST_STRIKE) shouldBe true
        }
    }
}
