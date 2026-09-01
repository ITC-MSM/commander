package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vulturous Zombie (RAV #238) — "Whenever a card is put into an opponent's graveyard from anywhere,
 * put a +1/+1 counter on this creature."
 *
 * The trigger is a hand-rolled `ZoneChangeEvent(to = GRAVEYARD)` with no `from` and an
 * owned-by-opponent filter, so the two things worth proving are the two the rulings single out: it
 * fires from *any* zone (the battlefield here, a library there), and it does **not** fire for cards
 * reaching the controller's own graveyard.
 */
class VulturousZombieScenarioTest : ScenarioTestBase() {

    init {
        context("Vulturous Zombie") {

            fun TestGame.zombieCounters(): Int = findPermanent("Vulturous Zombie")
                ?.let { state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) }
                ?: 0

            test("an opponent's creature dying from the battlefield grows it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Vulturous Zombie")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("the Zombie starts bare") { game.zombieCounters() shouldBe 0 }

                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("their creature card reached their graveyard") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.zombieCounters() shouldBe 1
                }
            }

            test("your own creature dying does not — the graveyard is keyed by owner") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Vulturous Zombie")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("your own graveyard is not an opponent's") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.zombieCounters() shouldBe 0
                }
            }

            test("milling an opponent's library grows it once per card — 'from anywhere'") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Vulturous Zombie")
                    .withCardInHand(1, "Tome Scour") // Target player mills five cards.
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Tome Scour", 2).error shouldBe null
                game.resolveStack()

                withClue("five cards into their graveyard is five counters") {
                    game.zombieCounters() shouldBe 5
                }
            }
        }
    }
}
