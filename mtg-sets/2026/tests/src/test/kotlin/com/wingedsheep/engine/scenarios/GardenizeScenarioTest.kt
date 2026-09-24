package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Gardenize (FRA #103) — {1}{G}{G} Enchantment.
 * "Whenever a creature you control dies, put a charge counter on this enchantment.
 *  At the beginning of your first main phase, add {G} for each charge counter on this enchantment."
 *
 * Only your own creatures feed it, and the mana is read from the counters at resolution.
 */
class GardenizeScenarioTest : ScenarioTestBase() {

    private fun TestGame.charge(gardenize: EntityId): Int =
        state.getEntity(gardenize)?.get<CountersComponent>()?.getCount(CounterType.CHARGE) ?: 0

    private fun TestGame.green(): Int =
        state.getEntity(player1Id)?.get<ManaPoolComponent>()?.green ?: 0

    private fun TestGame.bearsOf(playerId: EntityId): EntityId =
        state.getBattlefield(playerId).first { state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }

    private fun builder(): ScenarioBuilder {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Gardenize")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withCardsInHand(1, "Shock", 2)
            .withLandsOnBattlefield(1, "Mountain", 2)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(6) { b = b.withCardInLibrary(1, "Forest") }
        repeat(6) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Gardenize") {

            test("your creature dying adds a charge counter; an opponent's doesn't") {
                val game = builder().build()
                val gardenize = game.findPermanent("Gardenize")!!
                val theirs = game.bearsOf(game.player2Id)
                val mine = game.bearsOf(game.player1Id)

                game.castSpell(1, "Shock", theirs).error shouldBe null
                game.resolveStack()
                withClue("an opponent's creature dying doesn't count") { game.charge(gardenize) shouldBe 0 }

                game.castSpell(1, "Shock", mine).error shouldBe null
                game.resolveStack()
                withClue("your creature dying adds one charge counter") { game.charge(gardenize) shouldBe 1 }
            }

            test("the first-main trigger adds {G} per charge counter") {
                val game = builder().build()
                val gardenize = game.findPermanent("Gardenize")!!
                game.castSpell(1, "Shock", game.bearsOf(game.player1Id)).error shouldBe null
                game.resolveStack()
                game.charge(gardenize) shouldBe 1

                // Through the opponent's turn to our next first main phase.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.activePlayerId shouldBe game.player2Id
                game.resolveStack()
                withClue("the trigger is yours only; nothing on the opponent's turn") { game.green() shouldBe 0 }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                withClue("one charge counter → one {G}") { game.green() shouldBe 1 }
            }
        }
    }
}
