package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.PlagueBoiler
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Plague Boiler (RAV #269) — an upkeep fuse, a {1}{B}{G} ability that winds it either way, and a
 * state trigger that blows up the board at three plague counters.
 *
 * Two things are worth proving. The state-triggered ability (CR 603.8) must fire on the false → true
 * transition and actually sacrifice + wipe; and the `{1}{B}{G}` ability's "put **or** remove" must
 * really offer both directions, since removing a counter is the only way to hold the fuse back.
 */
class PlagueBoilerScenarioTest : ScenarioTestBase() {

    private val windAbility = PlagueBoiler.activatedAbilities.single().id

    private companion object {
        const val PUT_MODE = 0
        const val REMOVE_MODE = 1
    }

    init {
        context("Plague Boiler") {

            fun TestGame.plagueCounters(): Int = findPermanent("Plague Boiler")
                ?.let { state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.PLAGUE) }
                ?: 0

            fun TestGame.wind(mode: Int) {
                val boiler = findPermanent("Plague Boiler")!!
                val activation = execute(
                    ActivateAbility(playerId = player1Id, sourceId = boiler, abilityId = windAbility)
                )
                withClue("activation should succeed: ${activation.error}") { activation.error shouldBe null }
                resolveStack()
                getPendingDecision()?.let { decision ->
                    val choice = submitDecision(OptionChosenResponse(decision.id, mode))
                    withClue("mode choice should succeed: ${choice.error}") { choice.error shouldBe null }
                }
                resolveStack()
            }

            // CR 603.8 state triggers are polled when a player would receive priority, which in this
            // harness means letting both players pass off an empty stack so the step advances.
            fun TestGame.pollStateTriggers() {
                passPriority()
                passPriority()
                resolveStack()
            }

            test("the ability winds the fuse in both directions") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Plague Boiler")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.plagueCounters() shouldBe 0

                game.wind(PUT_MODE)
                withClue("the put mode adds one") { game.plagueCounters() shouldBe 1 }

                game.wind(REMOVE_MODE)
                withClue("the remove mode takes it back off") { game.plagueCounters() shouldBe 0 }
            }

            test("the third plague counter sacrifices it and destroys all nonland permanents") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Plague Boiler")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.wind(PUT_MODE)
                game.wind(PUT_MODE)
                withClue("two counters is below the threshold — nothing has happened yet") {
                    game.plagueCounters() shouldBe 2
                    game.findPermanent("Plague Boiler") shouldNotBe null
                    game.findAllPermanents("Grizzly Bears").size shouldBe 2
                }

                game.wind(PUT_MODE)
                game.pollStateTriggers()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("the Boiler sacrificed itself and took the board with it") {
                    game.findPermanent("Plague Boiler") shouldBe null
                    game.findAllPermanents("Grizzly Bears") shouldBe emptyList()
                }
                withClue("lands are untouched — it destroys nonland permanents only") {
                    game.findAllPermanents("Swamp").size shouldBe 5
                    game.findAllPermanents("Forest").size shouldBe 5
                }
            }
        }
    }
}
