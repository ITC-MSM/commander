package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Antiquities on the Loose — {1}{W}{W} Sorcery.
 * Create two 2/2 red and white Spirit creature tokens. Then if this spell was cast from anywhere
 * other than your hand, put a +1/+1 counter on each Spirit you control.
 * Flashback {4}{W}{W}.
 *
 * Covers the hand cast (two tokens, no counters) and the flashback cast (two tokens + a +1/+1
 * counter on every Spirit, since the graveyard origin satisfies "cast from anywhere other than your
 * hand").
 */
class AntiquitiesOnTheLooseScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Antiquities on the Loose") {

            test("hand cast makes two Spirit tokens and adds no counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Antiquities on the Loose")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Antiquities on the Loose").error shouldBe null
                game.resolveStack()

                val spirits = game.findPermanents("Spirit Token")
                withClue("Two Spirit tokens were created") {
                    spirits.size shouldBe 2
                }
                withClue("Hand cast → no +1/+1 counters") {
                    spirits.all { plusOneCounters(game, it) == 0 } shouldBe true
                }
            }

            test("flashback (graveyard) cast makes two Spirits and puts a +1/+1 counter on each Spirit you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Antiquities on the Loose")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellFromGraveyard(1, "Antiquities on the Loose").error shouldBe null
                game.resolveStack()

                val spirits = game.findPermanents("Spirit Token")
                withClue("Two Spirit tokens were created") {
                    spirits.size shouldBe 2
                }
                withClue("Non-hand cast → each Spirit gets one +1/+1 counter") {
                    spirits.all { plusOneCounters(game, it) == 1 } shouldBe true
                }
                withClue("Flashback exiles the card, so it leaves the graveyard") {
                    game.isInGraveyard(1, "Antiquities on the Loose") shouldBe false
                }
            }
        }
    }
}
