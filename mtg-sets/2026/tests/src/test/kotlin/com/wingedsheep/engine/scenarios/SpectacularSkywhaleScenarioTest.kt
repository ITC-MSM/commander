package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spectacular Skywhale {2}{U}{R} 1/4 Elemental Whale (Flying).
 *
 * "Opus — Whenever you cast an instant or sorcery spell, this creature gets +3/+0 until end of
 * turn. If five or more mana was spent to cast that spell, put three +1/+1 counters on this
 * creature instead."
 *
 * Opus ability word; the three +1/+1 counters *replace* the +3/+0 buff (insteadIfFiveOrMore).
 * Exercises the sub-5 +3/+0 (→ 4/4) and the 5+ boundary (→ +3/+3 from counters → 4/7).
 */
class SpectacularSkywhaleScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Spectacular Skywhale") {

            test("a cheap instant gives +3/+0 until end of turn (5+ tier not reached)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spectacular Skywhale") // 1/4
                    .withCardInHand(1, "Lightning Bolt") // {R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val whale = game.findPermanent("Spectacular Skywhale")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("1 mana spent → +3/+0 → 4/4") {
                    projector.getProjectedPower(game.state, whale) shouldBe 4
                    projector.getProjectedToughness(game.state, whale) shouldBe 4
                }
            }

            test("a 5-mana spell puts three +1/+1 counters INSTEAD (boundary: 4/7, not 4/4)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spectacular Skywhale") // 1/4
                    .withCardInHand(1, "Blaze") // {X}{R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val whale = game.findPermanent("Spectacular Skywhale")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                // Blaze X=4 → {4}{R} → 5 mana spent (boundary).
                game.castXSpell(1, "Blaze", xValue = 4, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("5 mana spent → three +1/+1 counters instead → 4/7 (1+3 / 4+3)") {
                    projector.getProjectedPower(game.state, whale) shouldBe 4
                    projector.getProjectedToughness(game.state, whale) shouldBe 7
                }
            }
        }
    }
}
