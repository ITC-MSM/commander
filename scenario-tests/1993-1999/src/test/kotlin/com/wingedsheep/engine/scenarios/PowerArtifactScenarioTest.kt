package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Power Artifact (ATQ #11).
 *
 * {U}{U} Aura — Enchant artifact. "Enchanted artifact's activated abilities cost {2} less to
 * activate. This effect can't reduce the mana in that cost to less than one mana."
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.ReduceActivatedAbilityCost] static keyed to the
 * enchanted permanent (`Scope.AttachedTo`). Asserts affordability of the enchanted artifact's
 * activated ability under a controlled number of mana sources, isolating the {2} generic reduction
 * and the one-mana floor.
 */
class PowerArtifactScenarioTest : ScenarioTestBase() {

    init {
        // Build an artifact + Power Artifact attached to it, with [islands] untapped Islands as the
        // only mana sources. Return whether the artifact's activated ability is affordable.
        fun abilityAffordableWith(
            artifactName: String,
            islands: Int,
            attachPowerArtifact: Boolean
        ): Boolean {
            val builder = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, artifactName)
                .withLandsOnBattlefield(1, "Island", islands)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            if (attachPowerArtifact) builder.withCardOnBattlefield(1, "Power Artifact")
            val game = builder.build()

            val artifact = game.findPermanent(artifactName)!!
            if (attachPowerArtifact) {
                val aura = game.findPermanent("Power Artifact")!!
                game.state = game.state.updateEntity(aura) { it.with(AttachedToComponent(artifact)) }
            }

            val actions = game.getLegalActions(1)
            val abilityAction = actions.firstOrNull {
                val a = it.action
                a is ActivateAbility && a.sourceId == artifact && !it.isManaAbility
            }
            return abilityAction?.isAffordable == true
        }

        context("Power Artifact reduces the enchanted artifact's activated-ability cost") {

            test("Millstone's {2} ability becomes affordable with a single Island when enchanted") {
                // Baseline: {2}{T} with one Island is NOT affordable.
                withClue("unenchanted: {2} needs two mana, one Island is not enough") {
                    abilityAffordableWith("Millstone", islands = 1, attachPowerArtifact = false) shouldBe false
                }
                // With Power Artifact: {2} − {2} floored at one mana → {1}, affordable with one Island.
                withClue("enchanted: {2} reduced to {1}, one Island suffices") {
                    abilityAffordableWith("Millstone", islands = 1, attachPowerArtifact = true) shouldBe true
                }
            }

            test("the one-mana floor: the reduced cost is {1}, never {0}") {
                // With Power Artifact but ZERO mana sources, Millstone's ability is still NOT
                // affordable — the {2} reduction is floored at one mana, not reduced to {0}.
                withClue("floor keeps the cost at {1}, so zero mana can't pay it") {
                    abilityAffordableWith("Millstone", islands = 0, attachPowerArtifact = true) shouldBe false
                }
            }

            test("a larger cost is reduced by exactly {2} (Obelisk of Undoing {6} -> {4})") {
                // Obelisk of Undoing is {6}{T}. Reduced by {2} → {4}: affordable with 4 Islands,
                // not with 3 — proving the reduction lands well above the floor.
                withClue("unenchanted {6} not affordable with 4 lands") {
                    abilityAffordableWith("Obelisk of Undoing", islands = 4, attachPowerArtifact = false) shouldBe false
                }
                withClue("enchanted {6} -> {4}: affordable with 4 lands") {
                    abilityAffordableWith("Obelisk of Undoing", islands = 4, attachPowerArtifact = true) shouldBe true
                }
                withClue("enchanted {6} -> {4}: NOT affordable with only 3 lands") {
                    abilityAffordableWith("Obelisk of Undoing", islands = 3, attachPowerArtifact = true) shouldBe false
                }
            }

            test("only the enchanted artifact is discounted, not other artifacts") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Millstone")       // enchanted
                    .withCardOnBattlefield(1, "Amulet of Kroog") // NOT enchanted ({2},{T})
                    .withCardOnBattlefield(1, "Power Artifact")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val millstone = game.findPermanent("Millstone")!!
                val amulet = game.findPermanent("Amulet of Kroog")!!
                val aura = game.findPermanent("Power Artifact")!!
                game.state = game.state.updateEntity(aura) { it.with(AttachedToComponent(millstone)) }

                val actions = game.getLegalActions(1)
                fun affordable(sourceId: com.wingedsheep.sdk.model.EntityId) = actions.firstOrNull {
                    val a = it.action
                    a is ActivateAbility && a.sourceId == sourceId && !it.isManaAbility
                }?.isAffordable == true

                withClue("enchanted Millstone {2}->{1}: affordable with one Island") {
                    affordable(millstone) shouldBe true
                }
                withClue("un-enchanted Amulet of Kroog {2}: NOT affordable with one Island") {
                    affordable(amulet) shouldBe false
                }
            }
        }
    }
}
