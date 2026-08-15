package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Glorious Sunrise (VOW #200).
 *
 * "{3}{G}{G} Enchantment
 *  At the beginning of combat on your turn, choose one —
 *  • Creatures you control get +1/+1 and gain trample until end of turn.
 *  • Target land gains "{T}: Add {G}{G}{G}" until end of turn.
 *  • Draw a card if you control a creature with power 3 or greater.
 *  • You gain 3 life."
 *
 * Exercises the begin-combat modal trigger across all four modes:
 *  - mode 1: the group pump (+1/+1 + trample) applies to every creature you control;
 *  - mode 2: the granted "{T}: Add {G}{G}{G}" adds an activation the bare land didn't have;
 *  - mode 3: the conditional draw fires only when you control a power-3+ creature;
 *  - mode 4: plain gain 3 life.
 */
class GloriousSunriseScenarioTest : ScenarioTestBase() {

    private val pumpMode = "Creatures you control get +1/+1 and gain trample until end of turn"
    private val landMode = "Target land gains \"{T}: Add {G}{G}{G}\" until end of turn"
    private val drawMode = "Draw a card if you control a creature with power 3 or greater"
    private val lifeMode = "You gain 3 life"

    /** Advance to begin-combat and drain the trigger onto the stack until the modal choice appears. */
    private fun TestGame.resolveToModeChoice(): ChooseOptionDecision {
        passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
        resolveStack()
        val decision = getPendingDecision()
        decision.shouldNotBeNull()
        return decision as ChooseOptionDecision
    }

    private fun TestGame.chooseMode(decision: ChooseOptionDecision, description: String) {
        val index = decision.options.indexOf(description)
        check(index >= 0) { "Mode '$description' not offered; options=${decision.options}" }
        submitDecision(OptionChosenResponse(decision.id, index))
    }

    init {
        /** Directly add +1/+1 counters to a battlefield entity without going through the stack. */
        fun addPlusOne(game: TestGame, id: EntityId, count: Int) {
            game.state = game.state.updateEntity(id) { container ->
                val existing = container.get<CountersComponent>() ?: CountersComponent()
                container.with(existing.withAdded(CounterType.PLUS_ONE_PLUS_ONE, count))
            }
        }

        context("Glorious Sunrise — begin-combat modal trigger") {

            test("mode 1: creatures you control get +1/+1 and gain trample") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glorious Sunrise")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 1) // gives mode 2 a legal land target
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val choice = game.resolveToModeChoice()
                withClue("all four modes are offered when a land is present for mode 2") {
                    choice.options.size shouldBe 4
                }
                game.chooseMode(choice, pumpMode)
                game.resolveStack()

                withClue("Grizzly Bears becomes 3/3 (+1/+1)") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 3
                }
                withClue("Grizzly Bears gains trample") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
                }
            }

            test("mode 2: target land gains a second tap-for-mana activation") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glorious Sunrise")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val before = game.getLegalActions(1)
                    .count { (it.action as? ActivateAbility)?.sourceId == forest }

                val choice = game.resolveToModeChoice()
                game.chooseMode(choice, landMode)
                // Mode 2 needs a target land.
                game.selectTargets(listOf(forest)).error shouldBe null
                game.resolveStack()

                val after = game.getLegalActions(1)
                    .count { (it.action as? ActivateAbility)?.sourceId == forest }
                withClue("the granted {T}: Add {G}{G}{G} is an activation the bare Forest didn't have") {
                    after shouldBe before + 1
                }
            }

            test("mode 3: draws a card when you control a power-3+ creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glorious Sunrise")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                addPlusOne(game, bears, 1) // 3/3 — meets the "power 3 or greater" condition.
                val handBefore = game.handSize(1)

                val choice = game.resolveToModeChoice()
                game.chooseMode(choice, drawMode)
                game.resolveStack()

                withClue("drew a card because a power-3+ creature is controlled") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("mode 3: does NOT draw when you control no power-3+ creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glorious Sunrise")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                val choice = game.resolveToModeChoice()
                game.chooseMode(choice, drawMode)
                game.resolveStack()

                withClue("no draw — the biggest creature is only power 2") {
                    game.handSize(1) shouldBe handBefore
                }
            }

            test("mode 4: you gain 3 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glorious Sunrise")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val choice = game.resolveToModeChoice()
                game.chooseMode(choice, lifeMode)
                game.resolveStack()

                withClue("life goes from 20 to 23") { game.getLifeTotal(1) shouldBe 23 }
            }
        }
    }
}
