package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Abstract Paintmage (Secrets of Strixhaven #171).
 *
 * "At the beginning of your first main phase, add {U}{R}. Spend this mana only to cast instant and
 * sorcery spells." Verifies that the first-main trigger adds exactly one restricted {U} and one
 * restricted {R}, both carrying [ManaRestriction.InstantOrSorceryOnly].
 */
class AbstractPaintmageScenarioTest : ScenarioTestBase() {

    init {
        context("Abstract Paintmage — first main phase mana") {
            test("adds restricted {U} and {R} at the beginning of the controller's first main phase") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Abstract Paintmage")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UPKEEP)
                    .build()

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.resolveStack()

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("Abstract Paintmage's trigger produces two pieces of restricted mana") {
                    pool!!.restrictedMana.size shouldBe 2
                }
                val colors = pool!!.restrictedMana.map { it.color }.toSet()
                withClue("one blue and one red") {
                    colors shouldBe setOf(Color.BLUE, Color.RED)
                }
                withClue("both restricted to instant/sorcery spells") {
                    pool.restrictedMana.all { it.restriction == ManaRestriction.InstantOrSorceryOnly } shouldBe true
                }
            }
        }
    }
}
