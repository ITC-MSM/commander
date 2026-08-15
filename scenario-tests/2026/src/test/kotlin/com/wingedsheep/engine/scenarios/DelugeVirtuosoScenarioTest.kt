package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Deluge Virtuoso {2}{U} 2/2 Human Wizard.
 *
 * ETB: "When this creature enters, tap target creature an opponent controls and put a stun counter
 * on it." Plus "Opus — Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until
 * end of turn. If five or more mana was spent to cast that spell, this creature gets +2/+2 until end
 * of turn instead." (the 5+ tier *replaces* the base via `insteadIfFiveOrMore`).
 */
class DelugeVirtuosoScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private fun stunCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    private fun isTapped(game: TestGame, id: EntityId): Boolean =
        game.state.getEntity(id)?.get<TappedComponent>() != null

    init {
        context("Deluge Virtuoso") {

            test("ETB taps an opponent's creature and puts a stun counter on it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Deluge Virtuoso")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Deluge Virtuoso").error shouldBe null
                game.resolveStack() // creature enters → ETB trigger asks for its target
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("ETB taps the targeted opponent creature") {
                    isTapped(game, bears) shouldBe true
                }
                withClue("ETB puts one stun counter on it") {
                    stunCounters(game, bears) shouldBe 1
                }
            }

            test("Opus: a cheap instant/sorcery gives +1/+1 until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Deluge Virtuoso") // 2/2
                    .withCardInHand(1, "Lightning Bolt") // {R}, 1 mana
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val virtuoso = game.findPermanent("Deluge Virtuoso")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("1 mana spent → base +1/+1 → 3/3") {
                    projector.getProjectedPower(game.state, virtuoso) shouldBe 3
                    projector.getProjectedToughness(game.state, virtuoso) shouldBe 3
                }
            }

            test("Opus: a 5+ mana spell gives +2/+2 INSTEAD (not stacked)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Deluge Virtuoso") // 2/2
                    .withCardInHand(1, "Blaze") // {X}{R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val virtuoso = game.findPermanent("Deluge Virtuoso")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                // Blaze X=4 → {4}{R} → 5 mana spent (boundary).
                game.castXSpell(1, "Blaze", xValue = 4, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("5 mana spent → +2/+2 instead → 4/4 (NOT 5/5, base is replaced)") {
                    projector.getProjectedPower(game.state, virtuoso) shouldBe 4
                    projector.getProjectedToughness(game.state, virtuoso) shouldBe 4
                }
            }
        }
    }
}
