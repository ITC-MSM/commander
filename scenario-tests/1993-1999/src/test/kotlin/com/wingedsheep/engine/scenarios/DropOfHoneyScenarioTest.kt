package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.FloatingEffectData
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Drop of Honey (ARN #47).
 *
 * "{G} Enchantment. At the beginning of your upkeep, destroy the creature with the least power.
 *  It can't be regenerated. If two or more creatures are tied for least power, you choose one of
 *  them. When there are no creatures on the battlefield, sacrifice this enchantment."
 *
 * Exercises the new global least-power selector (`HasLeastPowerAmongAllCreatures`) and the
 * `Conditions.NoCreaturesOnBattlefield` state trigger:
 *  - the destroy is global (it can hit an opponent's creature),
 *  - on a tie the controller chooses which least-power creature dies,
 *  - the destroy ignores regeneration shields ("It can't be regenerated"),
 *  - the enchantment sacrifices itself once the board is empty of creatures.
 */
class DropOfHoneyScenarioTest : ScenarioTestBase() {

    init {
        context("Drop of Honey upkeep destruction") {

            test("destroys the single least-power creature, even an opponent's") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Drop of Honey")
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)   // 3/3
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false) // 2/2 — least power
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                // Advance to the Drop of Honey controller's (player 1's) upkeep.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("Grizzly Bears (power 2) is the global least — destroyed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("Hill Giant (power 3) is not the least — survives") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("A creature remains, so Drop of Honey is not sacrificed") {
                    game.isOnBattlefield("Drop of Honey") shouldBe true
                }
            }

            test("on a tie for least power, the controller chooses which dies") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Drop of Honey")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2
                    .withCardOnBattlefield(1, "Gray Ogre", summoningSickness = false)     // 2/2 — tied least
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)    // 3/3
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("Two creatures tie for least power (2) — the controller must choose") {
                    game.getPendingDecision().shouldNotBeNull()
                }

                val grizzly = game.findPermanent("Grizzly Bears")!!
                game.selectCards(listOf(grizzly))
                game.resolveStack()

                withClue("The chosen creature is destroyed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("The other tied creature survives") {
                    game.isOnBattlefield("Gray Ogre") shouldBe true
                }
            }

            test("the destroy ignores regeneration shields (\"It can't be regenerated\")") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Drop of Honey")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2 — least power
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)    // 3/3
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                // With the destroy trigger pending, give the least-power creature a regeneration
                // shield (installed directly, after the turn change so cleanup can't expire it —
                // same idiom as AbuJafarTest).
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state.copy(
                    floatingEffects = game.state.floatingEffects +
                        regenerationShield(bears, game.player1Id)
                )

                game.resolveStack()

                withClue("\"It can't be regenerated\" — the shield must not save it") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
            }
        }

        context("Drop of Honey self-sacrifice") {

            test("is sacrificed when there are no creatures on the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Drop of Honey")
                    // No creatures anywhere — the state trigger should fire.
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("With no creatures on the battlefield, Drop of Honey sacrifices itself") {
                    game.isOnBattlefield("Drop of Honey") shouldBe false
                }
            }
        }
    }
}

private fun regenerationShield(entityId: EntityId, controllerId: EntityId) = ActiveFloatingEffect(
    id = EntityId.generate(),
    effect = FloatingEffectData(
        layer = Layer.ABILITY,
        modification = SerializableModification.RegenerationShield,
        affectedEntities = setOf(entityId)
    ),
    duration = Duration.EndOfTurn,
    sourceId = null,
    controllerId = controllerId,
    timestamp = 1L
)
