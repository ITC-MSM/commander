package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Dragonstorm Globe (TDM #241) — {3} Artifact.
 *
 * "Each Dragon you control enters with an additional +1/+1 counter on it.
 *  {T}: Add one mana of any color."
 *
 * Verifies the entering-Dragon replacement (Rule 614): a Dragon you control entering the
 * battlefield gains one extra +1/+1 counter, while a non-Dragon creature is unaffected.
 */
class DragonstormGlobeScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val permanent = game.findPermanent(name)!!
        return game.state.getEntity(permanent)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Dragon",
                manaCost = ManaCost.parse("{4}{R}"),
                subtypes = setOf(Subtype.DRAGON),
                power = 4,
                toughness = 4
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Soldier",
                manaCost = ManaCost.parse("{1}{W}"),
                subtypes = setOf(Subtype("Soldier")),
                power = 2,
                toughness = 2
            )
        )

        context("Dragonstorm Globe") {

            test("a Dragon you control enters with an extra +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dragonstorm Globe", summoningSickness = false)
                    .withCardInHand(1, "Test Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Dragon").error shouldBe null
                game.resolveStack()

                withClue("Test Dragon enters with one extra +1/+1 counter from Dragonstorm Globe") {
                    plusOneCounters(game, "Test Dragon") shouldBe 1
                }
            }

            test("a non-Dragon creature is unaffected") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dragonstorm Globe", summoningSickness = false)
                    .withCardInHand(1, "Test Soldier")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Soldier").error shouldBe null
                game.resolveStack()

                withClue("A non-Dragon creature enters with no +1/+1 counters") {
                    plusOneCounters(game, "Test Soldier") shouldBe 0
                }
            }
        }
    }
}
