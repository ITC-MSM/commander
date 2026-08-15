package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.RalZarekGuestLecturer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain

/**
 * Ral Zarek, Guest Lecturer.
 *
 * Covers the −2 reanimate (existing primitives) and the [SkipNextTurnComponent] turn-count
 * mechanic that backs the −7 ultimate. The coin-flip half of the ultimate is exercised by
 * [FlipCoinsEffectTest]; here we drive the deterministic skip-count directly so the assertion
 * isn't probabilistic.
 */
class RalZarekGuestLecturerScenarioTest : FunSpec({

    test("−2 returns a creature card with mana value 3 or less from your graveyard to the battlefield") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RalZarekGuestLecturer))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val ral = driver.putPermanentOnBattlefield(me, "Ral Zarek, Guest Lecturer")
        // The battlefield helper doesn't seed loyalty counters; give Ral its starting loyalty.
        driver.replaceState(
            driver.state.updateEntity(ral) {
                it.with(
                    com.wingedsheep.engine.state.components.battlefield.CountersComponent(
                        mapOf(com.wingedsheep.sdk.core.CounterType.LOYALTY to 3)
                    )
                )
            }
        )
        // Savannah Lions ({W}, MV 1) is a legal target; put it in the graveyard.
        val bears = driver.putCardInGraveyard(me, "Savannah Lions")

        val minus2 = driver.cardRegistry.requireCard("Ral Zarek, Guest Lecturer")
            .activatedAbilities.first {
                (it.cost as? com.wingedsheep.sdk.scripting.AbilityCost.Loyalty)?.change == -2
            }

        driver.submit(
            ActivateAbility(
                playerId = me,
                sourceId = ral,
                abilityId = minus2.id,
                targets = listOf(
                    com.wingedsheep.engine.state.components.stack.ChosenTarget.Card(
                        cardId = bears, ownerId = me, zone = Zone.GRAVEYARD,
                    )
                ),
            )
        ).error shouldBe null

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        // Bears is now a permanent on the battlefield under my control.
        driver.state.getBattlefield() shouldContain bears
        driver.state.getEntity(bears)?.get<ControllerComponent>()?.playerId shouldBe me
    }

    test("SkipNextTurnComponent with turns = 2 skips that player's next two turns") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val first = driver.activePlayer!!
        val second = driver.getOpponent(first)

        // The second player will skip their next two turns.
        driver.replaceState(
            driver.state.updateEntity(second) { it.with(SkipNextTurnComponent(2)) }
        )

        // End turns until the second player finally takes a turn, counting how many turn
        // transitions it took and how the skip count drains.
        val skipCountsObserved = mutableListOf<Int?>()
        var transitions = 0
        while (driver.activePlayer != second && transitions < 10) {
            driver.passPriorityUntil(Step.END)
            driver.bothPass()
            transitions++
            skipCountsObserved.add(driver.state.getEntity(second)?.get<SkipNextTurnComponent>()?.turns)
        }

        // The second player only becomes active once both skipped turns have been consumed.
        driver.activePlayer shouldBe second
        // The skip count drained 2 -> 1 -> 0(removed) across the first player's intervening turns.
        skipCountsObserved.shouldContain(1)
        driver.state.getEntity(second)?.get<SkipNextTurnComponent>() shouldBe null
    }
})
