package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class ElderCatharScenarioTest : ScenarioTestBase() {
    init {
        test("dies putting two +1/+1 counters on a Human") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Elder Cathar")
                .withCardOnBattlefield(1, "Glory Seeker")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cathar = game.findPermanent("Elder Cathar")!!
            val human = game.findPermanent("Glory Seeker")!!
            val shock = game.findCardsInHand(1, "Shock").single()

            val cast = game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = shock,
                    targets = listOf(entityIdToChosenTarget(game.state, cathar)),
                ),
            )
            withClue("Shock cast: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            withClue("dies trigger should ask for a creature you control") {
                (game.state.pendingDecision as? ChooseTargetsDecision).shouldNotBeNull()
            }
            game.selectTargets(listOf(human))
            game.resolveStack()

            game.isOnBattlefield("Elder Cathar") shouldBe false
            val counters = game.state.getEntity(human)
                ?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
            counters shouldBe 2
        }

        test("dies putting one +1/+1 counter on a non-Human") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Elder Cathar")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cathar = game.findPermanent("Elder Cathar")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val shock = game.findCardsInHand(1, "Shock").single()

            val cast = game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = shock,
                    targets = listOf(entityIdToChosenTarget(game.state, cathar)),
                ),
            )
            withClue("Shock cast: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            (game.state.pendingDecision as? ChooseTargetsDecision).shouldNotBeNull()
            game.selectTargets(listOf(bears))
            game.resolveStack()

            val counters = game.state.getEntity(bears)
                ?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
            counters shouldBe 1
        }
    }
}
