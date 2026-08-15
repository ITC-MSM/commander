package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Molten-Core Maestro (Secrets of Strixhaven).
 *
 * Opus — Whenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature. If
 * five or more mana was spent to cast that spell, add an amount of {R} equal to this creature's
 * power.
 *
 * The base effect (+1/+1 counter) always fires; the `alsoIfFiveOrMore` mana production only fires
 * at 5+ mana, and reads the creature's power *after* the counter is added.
 */
class MoltenCoreMaestroScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private fun ManaPoolComponent.total() =
        white + blue + black + red + green + colorless

    init {
        test("a cheap spell adds a +1/+1 counter but produces no mana (5+ tier not reached)") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Molten-Core Maestro") // 2/2
                .withCardInHand(1, "Lightning Bolt") // {R}
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val maestro = game.findPermanent("Molten-Core Maestro")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
            game.resolveStack()

            withClue("1 mana spent → +1/+1 counter only → 3/3") {
                projector.getProjectedPower(game.state, maestro) shouldBe 3
                projector.getProjectedToughness(game.state, maestro) shouldBe 3
            }
            withClue("no mana added below the 5-mana threshold") {
                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                (pool?.total() ?: 0) shouldBe 0
            }
        }

        test("a 5-mana spell adds a +1/+1 counter AND produces {R} equal to power (now 3)") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Molten-Core Maestro") // 2/2
                .withCardInHand(1, "Blaze") // {X}{R}
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val maestro = game.findPermanent("Molten-Core Maestro")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            // Blaze X=4 → {4}{R} → 5 mana spent (boundary).
            game.castXSpell(1, "Blaze", xValue = 4, targetId = bears).error shouldBe null
            game.resolveStack()

            withClue("5 mana spent → +1/+1 counter → 3/3") {
                projector.getProjectedPower(game.state, maestro) shouldBe 3
                projector.getProjectedToughness(game.state, maestro) shouldBe 3
            }
            withClue("add {R} equal to power AFTER the counter → 3 red mana") {
                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()!!
                pool.red shouldBe 3
            }
        }
    }
}
