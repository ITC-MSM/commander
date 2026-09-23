package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Generous Revival (FRA #8) — {2}{W} Sorcery.
 *
 * "Return target creature card with mana value 3 or less from your graveyard to the battlefield
 *  with an additional +1/+1 counter on it. / Flashback {4}{W}"
 *
 * Asserts the entry counter lands on the returned permanent (via the move's `addCounterType`)
 * and that the mana-value gate on the target holds.
 */
class GenerousRevivalScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Generous Revival") {

            test("returns a cheap creature card with a +1/+1 counter") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Generous Revival")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .build()

                game.castSpellTargetingGraveyardCard(1, "Generous Revival", 1, "Grizzly Bears")
                    .error shouldBe null
                game.resolveStack()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.isInGraveyard(1, "Grizzly Bears") shouldBe false

                withClue("one +1/+1 counter on the returned creature") {
                    game.state.getEntity(bears)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                val projected = projector.project(game.state)
                projected.getPower(bears) shouldBe 3
                projected.getToughness(bears) shouldBe 3
            }

            test("a creature card with mana value 4 or more is not a legal target") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Generous Revival")
                    .withCardInGraveyard(1, "Serra Angel")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .build()

                game.castSpellTargetingGraveyardCard(1, "Generous Revival", 1, "Serra Angel")
                    .error shouldNotBe null
                game.isInGraveyard(1, "Serra Angel") shouldBe true
            }
        }
    }
}
