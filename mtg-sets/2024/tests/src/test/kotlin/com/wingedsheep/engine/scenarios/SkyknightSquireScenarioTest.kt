package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Skyknight Squire ({1}{W}, 1/1 Cat Scout).
 *
 * - Whenever another creature you control enters, put a +1/+1 counter on this creature.
 * - As long as it has three or more +1/+1 counters, it has flying and is a Knight in
 *   addition to its other types.
 *
 * Verifies the grow trigger fires on another creature entering, and the threshold static
 * abilities (flying + Knight subtype) turn on only at 3+ counters.
 */
class SkyknightSquireScenarioTest : ScenarioTestBase() {

    init {
        context("Skyknight Squire") {

            test("gains a +1/+1 counter when another creature you control enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Skyknight Squire")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val squire = game.findPermanent("Skyknight Squire")!!
                withClue("base 1/1 before any creature enters") {
                    game.state.projectedState.getPower(squire) shouldBe 1
                    game.state.projectedState.getToughness(squire) shouldBe 1
                }

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears entering puts a +1/+1 counter on the Squire → 2/2") {
                    game.state.projectedState.getPower(squire) shouldBe 2
                    game.state.projectedState.getToughness(squire) shouldBe 2
                }
            }

            test("below threshold: two counters grant neither flying nor Knight") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Skyknight Squire")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val squire = game.findPermanent("Skyknight Squire")!!
                game.state = game.state.updateEntity(squire) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 2)))
                }

                withClue("two counters is below the three-counter threshold") {
                    game.state.projectedState.hasKeyword(squire, Keyword.FLYING) shouldBe false
                    game.state.projectedState.hasSubtype(squire, "Knight") shouldBe false
                    game.state.projectedState.hasSubtype(squire, "Cat") shouldBe true
                }
            }

            test("at threshold: three counters grant flying and the Knight subtype in addition to its types") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Skyknight Squire")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val squire = game.findPermanent("Skyknight Squire")!!
                game.state = game.state.updateEntity(squire) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 3)))
                }

                withClue("three counters → flying + Knight, while keeping Cat and Scout") {
                    game.state.projectedState.hasKeyword(squire, Keyword.FLYING) shouldBe true
                    game.state.projectedState.hasSubtype(squire, "Knight") shouldBe true
                    game.state.projectedState.hasSubtype(squire, "Cat") shouldBe true
                    game.state.projectedState.hasSubtype(squire, "Scout") shouldBe true
                }
            }
        }
    }
}
