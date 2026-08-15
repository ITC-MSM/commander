package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Armageddon Clock (ATQ #37).
 *
 * {6} Artifact
 * "At the beginning of your upkeep, put a doom counter on this artifact.
 *  At the beginning of your draw step, this artifact deals damage equal to the number of doom
 *  counters on it to each player.
 *  {4}: Remove a doom counter from this artifact. Any player may activate this ability but only
 *  during any upkeep step."
 *
 * Exercises (a) doom-counter accrual each upkeep, (b) escalating damage to each player in the
 * controller's draw step, and (c) the any-player / upkeep-only {4} remove-a-counter ability —
 * including an opponent removing one during an upkeep and being unable to outside upkeep.
 */
class ArmageddonClockScenarioTest : ScenarioTestBase() {

    private val abilityId by lazy {
        cardRegistry.getCard("Armageddon Clock")!!.script.activatedAbilities[0].id
    }

    private fun doom(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.DOOM) ?: 0

    init {
        context("Armageddon Clock") {

            test("accrues a doom counter each upkeep and deals escalating damage in the draw step") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Armageddon Clock")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                repeat(10) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(10) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val clock = game.findPermanent("Armageddon Clock")!!

                // First upkeep: one doom counter accrues.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                withClue("One doom counter after the first upkeep") { doom(game, clock) shouldBe 1 }

                // Draw step: deals 1 (the counter count) to each player.
                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()
                withClue("Both players take 1 damage in the controller's draw step") {
                    game.getLifeTotal(1) shouldBe 19
                    game.getLifeTotal(2) shouldBe 19
                }

                // Advance to the controller's NEXT turn upkeep — a second counter accrues.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // opponent's upkeep (no accrual)
                game.resolveStack()
                withClue("No accrual on the opponent's upkeep — trigger is 'your upkeep'") {
                    doom(game, clock) shouldBe 1
                }
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // controller's upkeep again
                game.resolveStack()
                withClue("Second doom counter on the controller's next upkeep") {
                    doom(game, clock) shouldBe 2
                }

                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()
                withClue("With 2 counters, each player takes 2 more damage (19 -> 17)") {
                    game.getLifeTotal(1) shouldBe 17
                    game.getLifeTotal(2) shouldBe 17
                }
            }

            test("an opponent may remove a doom counter by paying {4} during an upkeep") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Armageddon Clock")
                    // Opponent's mana to pay the {4}.
                    .withLandsOnBattlefield(2, "Forest", 4)
                    // Start on the opponent's turn so a clean step transition into player 1's
                    // upkeep fires the accrual trigger.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(8) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(8) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val clock = game.findPermanent("Armageddon Clock")!!
                // Advance into the controller's (player 1's) upkeep and resolve the accrual trigger.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                withClue("One counter accrued on the controller's upkeep") { doom(game, clock) shouldBe 1 }

                // The active player (player 1) passes priority so the opponent (player 2) gets it
                // and may use the "any player may activate" ability during this upkeep.
                if (game.state.priorityPlayerId == game.player1Id) game.passPriority()

                // "Any player may activate" surfaces as a legal action for the opponent even though
                // they don't control the Clock — drive the engine through that enumerated action.
                val clockAction = game.getLegalActions(2).map { it.action }.firstOrNull {
                    it is ActivateAbility && it.sourceId == clock && it.abilityId == abilityId
                }
                withClue("The opponent has a legal 'remove a doom counter' action during this upkeep, step=${game.state.step}") {
                    (clockAction != null) shouldBe true
                }
                val result = game.execute(clockAction!!)
                withClue("The opponent may activate the {4} ability during this upkeep: error=${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("The doom counter was removed") { doom(game, clock) shouldBe 0 }
            }

            test("the {4} ability is not a legal action for the opponent outside an upkeep step") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Armageddon Clock")
                    .withLandsOnBattlefield(2, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val clock = game.findPermanent("Armageddon Clock")!!
                val clockActions = game.getLegalActions(2).filter {
                    (it.action as? ActivateAbility)?.sourceId == clock
                }
                withClue("Armageddon Clock's ability must not be activatable outside any upkeep step") {
                    clockActions shouldBe emptyList()
                }
            }
        }
    }
}
