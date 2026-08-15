package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Petrify (LCI #30) — {1}{W} Enchantment — Aura.
 *
 *   Enchant artifact or creature
 *   Enchanted permanent can't attack or block, and its activated abilities can't be
 *   activated.
 *
 * Combines Pacifism's combat lock (`CantAttack`/`CantBlock` scoped to the attached
 * permanent) with the Cursed Totem-style activation lock scoped to just this Aura's host
 * (`PreventActivatedAbilities(GameObjectFilter.Permanent.attachedToBySource())`). Because
 * the Aura enchants an artifact or creature, both halves must land on the enchanted
 * permanent.
 */
class PetrifyScenarioTest : ScenarioTestBase() {

    private val elvesManaAbilityId =
        cardRegistry.getCard("Llanowar Elves")!!.script.activatedAbilities.first().id

    init {
        context("Petrify") {

            test("enchanted creature can't attack or block") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Petrify", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Petrify should be on the battlefield") {
                    game.isOnBattlefield("Petrify") shouldBe true
                }
                withClue("Enchanted creature can't attack") {
                    game.state.projectedState.cantAttack(bears) shouldBe true
                }
                withClue("Enchanted creature can't block") {
                    game.state.projectedState.cantBlock(bears) shouldBe true
                }
            }

            test("enchanted creature's activated abilities can't be activated") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Llanowar Elves", summoningSickness = false)
                    .withCardAttachedTo(1, "Petrify", "Llanowar Elves")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                val elvesActivation = game.getLegalActions(2).find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == elves && a.abilityId == elvesManaAbilityId
                }
                withClue("Petrify should lock the enchanted creature's mana ability") {
                    elvesActivation shouldBe null
                }
            }

            test("control: without Petrify the creature can attack and activate abilities") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Llanowar Elves", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                withClue("Without the Aura the creature can attack") {
                    game.state.projectedState.cantAttack(elves) shouldBe false
                }
                val elvesActivation = game.getLegalActions(2).find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == elves && a.abilityId == elvesManaAbilityId
                }
                withClue("Without the Aura the mana ability should be available") {
                    (elvesActivation != null) shouldBe true
                }
            }
        }
    }
}
