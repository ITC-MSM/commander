package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Command the Stage (FRA #77) — makes a Cadet and grows the *other* Wizard tokens; returns from the
 * graveyard at the beginning of each upkeep if an opponent was dealt noncombat damage last turn.
 */
class CommandTheStageScenarioTest : ScenarioTestBase() {
    init {
        fun plusOnes(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        test("the new Cadet gets no counter; earlier Wizard tokens do, nontoken Wizards don't") {
            val game = scenario().withPlayers()
                .withCardsInHand(1, "Command the Stage", 2)
                .withCardOnBattlefield(1, "Prodigal Sorcerer")
                .withLandsOnBattlefield(1, "Mountain", 6)
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Command the Stage").error shouldBe null
            game.resolveStack()
            val first = game.findPermanents("Cadet").single()
            plusOnes(game, first) shouldBe 0

            game.castSpell(1, "Command the Stage").error shouldBe null
            game.resolveStack()
            val cadets = game.findPermanents("Cadet")
            val second = cadets.single { it != first }
            cadets.map { plusOnes(game, it) } shouldContainExactlyInAnyOrder listOf(1, 0)
            plusOnes(game, first) shouldBe 1
            plusOnes(game, second) shouldBe 0
            plusOnes(game, game.findPermanent("Prodigal Sorcerer")!!) shouldBe 0
        }

        fun graveyardGame() = scenario().withPlayers()
            .withCardInGraveyard(1, "Command the Stage")
            .withCardInHand(1, "Shock")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("returns at the next upkeep — the opponent's — after an opponent was dealt noncombat damage") {
            val game = graveyardGame()
            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()
            withClue("this turn's damage isn't last turn's yet") {
                game.isInGraveyard(1, "Command the Stage") shouldBe true
            }

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player2Id
            game.resolveStack()

            game.isInHand(1, "Command the Stage") shouldBe true
        }

        test("stays in the graveyard without noncombat damage to an opponent last turn") {
            val game = graveyardGame()
            game.castSpellTargetingPlayer(1, "Shock", 1).error shouldBe null
            game.resolveStack()

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.resolveStack()

            game.isInGraveyard(1, "Command the Stage") shouldBe true
        }

        test("the record covers one turn only") {
            val game = graveyardGame()
            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()
            // Nothing happens on the opponent's turn, so by the upkeep after it "last turn" is clean.
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.resolveStack()
            game.isInHand(1, "Command the Stage") shouldBe true
            game.state.playersDealtNoncombatDamageLastTurn shouldBe setOf(game.player2Id)

            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player1Id
            game.state.playersDealtNoncombatDamageLastTurn shouldBe emptySet()
        }
    }
}
