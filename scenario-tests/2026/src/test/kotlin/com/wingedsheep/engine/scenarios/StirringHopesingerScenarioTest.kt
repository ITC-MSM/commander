package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stirring Hopesinger (SOS #35, {2}{W} 1/3 Flying, lifelink).
 *
 *   Repartee — Whenever you cast an instant or sorcery spell that targets a creature,
 *   put a +1/+1 counter on each creature you control.
 *
 * Verifies the Repartee trigger (`Triggers.youCastSpell` over an instant/sorcery that targets a
 * creature) fires on a creature-targeting spell and counters every creature you control —
 * including Stirring Hopesinger itself — and does NOT fire on a non-targeting spell.
 */
class StirringHopesingerScenarioTest : ScenarioTestBase() {

    private fun counters(game: TestGame, entityId: EntityId): Int =
        game.state.getEntity(entityId)
            ?.get<CountersComponent>()
            ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Stirring Hopesinger") {

            test("casting an instant that targets a creature counters each creature you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Stirring Hopesinger")
                    .withCardOnBattlefield(1, "Grizzly Bears")     // another creature you control
                    .withCardInHand(1, "Giant Growth")             // {G} instant — targets a creature
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hopesinger = game.findPermanent("Stirring Hopesinger")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                // Target Grizzly Bears with Giant Growth (a creature target).
                game.castSpell(1, "Giant Growth", bears).error shouldBe null
                game.resolveStack()

                withClue("Stirring Hopesinger gets a +1/+1 counter (each creature you control includes itself)") {
                    counters(game, hopesinger) shouldBe 1
                }
                withClue("Grizzly Bears gets a +1/+1 counter") {
                    counters(game, bears) shouldBe 1
                }
            }

            test("casting a sorcery that does not target a creature does not trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Stirring Hopesinger")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Divination")               // {2}{U} sorcery — no target
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hopesinger = game.findPermanent("Stirring Hopesinger")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                withClue("No counter on Stirring Hopesinger — Divination has no creature target") {
                    counters(game, hopesinger) shouldBe 0
                }
                withClue("No counter on Grizzly Bears") {
                    counters(game, bears) shouldBe 0
                }
            }
        }
    }
}
