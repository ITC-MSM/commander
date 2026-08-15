package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Marvin, Murderous Mimic (DSK #253) — {2} 2/2 Legendary Artifact Creature — Toy.
 *
 * "Marvin has all activated abilities of creatures you control that don't have the same name as
 *  this creature."
 *
 * Modeled with [GainActivatedAbilitiesOfPermanents] (grantedTo = source, sourceFilter = creatures
 * you control not named Marvin, includeManaAbilities = true). Unlike Sharkey, the oracle text says
 * *all* activated abilities — mana abilities are included.
 *
 * Donor creatures: Llanowar Elves ({T}: Add {G}, a mana ability) and Prodigal Sorcerer
 * ({T}: deal 1 damage, a non-mana ability).
 */
class MarvinMurderousMimicScenarioTest : ScenarioTestBase() {

    init {
        context("Marvin gains other creatures' activated abilities") {

            test("Marvin gains a non-mana activated ability of another creature you control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Marvin, Murderous Mimic", summoningSickness = false)
                    .withCardOnBattlefield(1, "Prodigal Sorcerer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val marvin = game.findPermanent("Marvin, Murderous Mimic")!!
                val timDef = cardRegistry.getCard("Prodigal Sorcerer")!!
                val pingAbility = timDef.script.activatedAbilities.first()

                val legal = game.getLegalActions(1)
                val marvinPing = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == marvin && a.abilityId == pingAbility.id
                }
                withClue("Marvin should have gained Prodigal Sorcerer's ping ability") {
                    (marvinPing != null).shouldBeTrue()
                }
            }

            test("Marvin gains a mana ability of another creature you control (all activated abilities)") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Marvin, Murderous Mimic", summoningSickness = false)
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val marvin = game.findPermanent("Marvin, Murderous Mimic")!!
                val elvesDef = cardRegistry.getCard("Llanowar Elves")!!
                val manaAbility = elvesDef.script.activatedAbilities.first { it.isManaAbility }

                val legal = game.getLegalActions(1)
                val marvinMana = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == marvin && a.abilityId == manaAbility.id
                }
                withClue("Marvin includes mana abilities ('all activated abilities')") {
                    (marvinMana != null).shouldBeTrue()
                }
            }

            test("Marvin does NOT gain abilities of a creature an opponent controls") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Marvin, Murderous Mimic", summoningSickness = false)
                    .withCardOnBattlefield(2, "Prodigal Sorcerer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val marvin = game.findPermanent("Marvin, Murderous Mimic")!!
                val timDef = cardRegistry.getCard("Prodigal Sorcerer")!!
                val pingAbility = timDef.script.activatedAbilities.first()

                val legal = game.getLegalActions(1)
                val marvinPing = legal.find {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == marvin && a.abilityId == pingAbility.id
                }
                withClue("Marvin only copies creatures YOU control") {
                    marvinPing shouldBe null
                }
            }

            test("a lone Marvin (no other creatures) has no activated abilities to gain") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Marvin, Murderous Mimic", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Marvin's own card has no activated abilities, and "creatures you control that
                // don't have the same name as this creature" matches nothing (only Marvin himself
                // is out, and he's excluded by the name predicate). So Marvin offers no activations.
                val marvin = game.findPermanent("Marvin, Murderous Mimic")!!
                val legal = game.getLegalActions(1)
                val marvinActivations = legal.filter {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == marvin
                }
                withClue("a lone Marvin grants himself no abilities (name predicate excludes himself)") {
                    marvinActivations shouldBe emptyList()
                }
            }
        }
    }
}
