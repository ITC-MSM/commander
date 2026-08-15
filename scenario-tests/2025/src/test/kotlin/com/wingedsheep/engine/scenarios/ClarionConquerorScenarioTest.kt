package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Clarion Conqueror (Tarkir: Dragonstorm):
 * "Activated abilities of artifacts, creatures, and planeswalkers can't be activated."
 *
 * Reuses the [com.wingedsheep.sdk.scripting.PreventActivatedAbilities] primitive with an OR
 * filter over the three affected card types. Confirms creature, artifact, and planeswalker
 * (loyalty) abilities are blocked, that land mana abilities remain available, and that the
 * lockdown lifts once the Conqueror leaves the battlefield.
 */
class ClarionConquerorScenarioTest : ScenarioTestBase() {

    init {
        context("Clarion Conqueror blocks artifact, creature, and planeswalker abilities") {
            test("a creature's mana ability is not legal while Clarion Conqueror is in play") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Clarion Conqueror", summoningSickness = false)
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                val legal = game.getLegalActions(1)
                val activation = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == elves
                }
                withClue("Llanowar Elves' mana ability should be blocked") {
                    activation shouldBe null
                }
            }

            test("an artifact's activated ability is not legal while Clarion Conqueror is in play") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Clarion Conqueror", summoningSickness = false)
                    .withCardOnBattlefield(1, "Mind Stone", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mindStone = game.findPermanent("Mind Stone")!!
                val legal = game.getLegalActions(1)
                val activation = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == mindStone
                }
                withClue("Mind Stone's mana ability should be blocked") {
                    activation shouldBe null
                }
            }

            test("a planeswalker's loyalty abilities are not legal while Clarion Conqueror is in play") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Clarion Conqueror", summoningSickness = false)
                    .withCardOnBattlefield(1, "Sarkhan, the Dragonspeaker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sarkhan = game.findPermanent("Sarkhan, the Dragonspeaker")!!
                val legal = game.getLegalActions(1)
                val activation = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == sarkhan
                }
                withClue("Sarkhan's loyalty abilities should be blocked") {
                    activation shouldBe null
                }
            }
        }

        context("Clarion Conqueror leaves the affected types narrow") {
            test("land mana abilities are unaffected") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Clarion Conqueror", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val legal = game.getLegalActions(1)
                val activation = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == forest
                }
                withClue("Forest's mana ability should remain available") {
                    (activation != null) shouldBe true
                }
            }

            test("a creature's mana ability returns once Clarion Conqueror is gone") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                val legal = game.getLegalActions(1)
                val activation = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == elves
                }
                withClue("Without Clarion Conqueror, Llanowar Elves' mana ability should be available") {
                    (activation != null) shouldBe true
                }
            }
        }
    }
}
