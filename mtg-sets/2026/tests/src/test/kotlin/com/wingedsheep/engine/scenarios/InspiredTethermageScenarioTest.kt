package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CountersAddedEvent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Inspired Tethermage (FRA #109) — {2}{G} Creature — Elf Warrior 3/2:
 *   Whenever you put one or more loyalty counters on a planeswalker, put a +1/+1 counter on this
 *   creature.
 *   {6}: Empower Jace 2.
 *
 * Each way "you put loyalty counters on a planeswalker" happens is pinned separately: paying a
 * [+N] loyalty cost (CR 606.4 — the engine now reports it as a counter placement), casting a
 * planeswalker that enters with loyalty (CR 122.6), and the card's own empower Jace. A [−N] cost
 * removes counters and must not trigger it.
 */
class InspiredTethermageScenarioTest : ScenarioTestBase() {

    private fun TestGame.counters(id: EntityId, type: CounterType): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    private fun jaceAbility(change: Int) = cardRegistry.getCard("Jace Beleren")!!.script.activatedAbilities
        .single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

    private fun boardWithJace(): TestGame = scenario().withPlayers()
        .withCardOnBattlefield(1, "Inspired Tethermage")
        .withCardOnBattlefield(1, "Jace Beleren")
        .withCardInLibrary(1, "Forest")
        .withCardInLibrary(1, "Forest")
        .withCardInLibrary(2, "Forest")
        .withCardInLibrary(2, "Forest")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("a [+2] loyalty cost is putting loyalty counters on a planeswalker") {
            val game = boardWithJace()
            val tethermage = game.findPermanent("Inspired Tethermage")!!
            val jace = game.findPermanent("Jace Beleren")!!

            val result = game.execute(ActivateAbility(game.player1Id, jace, jaceAbility(2)))
            result.error shouldBe null
            withClue("the cost is reported as a loyalty-counter placement by the activating player") {
                val placed = result.events.filterIsInstance<CountersAddedEvent>()
                    .single { it.entityId == jace && it.counterType == Counters.LOYALTY }
                placed.amount shouldBe 2
                placed.placedBy shouldBe game.player1Id
            }
            game.resolveStack()

            game.counters(jace, CounterType.LOYALTY) shouldBe 5
            game.counters(tethermage, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            game.state.projectedState.getPower(tethermage) shouldBe 4
        }

        test("a [−1] loyalty cost removes counters and doesn't trigger it") {
            val game = boardWithJace()
            val tethermage = game.findPermanent("Inspired Tethermage")!!
            val jace = game.findPermanent("Jace Beleren")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, jace, jaceAbility(-1),
                    targets = listOf(ChosenTarget.Player(game.player1Id))
                )
            ).error shouldBe null
            game.resolveStack()

            game.counters(jace, CounterType.LOYALTY) shouldBe 2
            game.counters(tethermage, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 0
        }

        test("casting a planeswalker puts its loyalty counters on as it enters") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Inspired Tethermage")
                .withCardInHand(1, "Jace Beleren")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val tethermage = game.findPermanent("Inspired Tethermage")!!

            game.castSpell(1, "Jace Beleren").error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Jace Beleren") shouldBe true
            game.counters(tethermage, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        }

        test("{6}: Empower Jace 2 creates a Jace token with two loyalty, which triggers it") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Inspired Tethermage")
                .withLandsOnBattlefield(1, "Forest", 6)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val tethermage = game.findPermanent("Inspired Tethermage")!!
            val empower = cardRegistry.getCard("Inspired Tethermage")!!.script.activatedAbilities.single().id

            game.execute(ActivateAbility(game.player1Id, tethermage, empower)).error shouldBe null
            game.resolveStack()

            val jaceToken = game.findPermanent("Jace")
            jaceToken shouldNotBe null
            game.counters(jaceToken!!, CounterType.LOYALTY) shouldBe 2
            game.counters(tethermage, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        }
    }
}
