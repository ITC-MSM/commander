package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Smile at Death (TDM #24).
 *
 * "{3}{W}{W} Enchantment.
 *  At the beginning of your upkeep, return up to two target creature cards with power 2 or
 *  less from your graveyard to the battlefield. Put a +1/+1 counter on each of those creatures."
 */
class SmileAtDeathScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Weak One",
                manaCost = ManaCost.parse("{W}"),
                subtypes = setOf(Subtype("Soldier")),
                power = 1,
                toughness = 1
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Big One",
                manaCost = ManaCost.parse("{4}{G}"),
                subtypes = setOf(Subtype("Beast")),
                power = 5,
                toughness = 5
            )
        )

        fun plusOne(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        context("Smile at Death upkeep") {

            test("returns two power-2-or-less creature cards and puts a +1/+1 counter on each") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Smile at Death")
                    .withCardInGraveyard(1, "Weak One")
                    .withCardInGraveyard(1, "Grizzly Bears") // 2/2 — power 2, eligible
                    .withCardInGraveyard(1, "Big One")        // power 5, ineligible
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Plains") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Plains") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                fun gyCard(name: String) = game.state.getGraveyard(game.player1Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == name }

                if (game.hasPendingDecision()) {
                    game.selectTargets(listOf(gyCard("Weak One"), gyCard("Grizzly Bears")))
                    game.resolveStack()
                }

                val weakBf = game.findPermanent("Weak One")!!
                val bearsBf = game.findPermanent("Grizzly Bears")!!
                withClue("Weak One was returned to the battlefield") { weakBf shouldBe weakBf }
                withClue("Each returned creature got a +1/+1 counter") {
                    plusOne(game, weakBf) shouldBe 1
                    plusOne(game, bearsBf) shouldBe 1
                }
                withClue("The power-5 creature was not a legal target and stayed in the graveyard") {
                    game.findPermanent("Big One") shouldBe null
                }
            }
        }
    }
}
