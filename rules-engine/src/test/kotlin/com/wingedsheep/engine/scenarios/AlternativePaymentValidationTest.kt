package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import com.wingedsheep.sdk.scripting.ConvokePayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * The engine, not the client, decides what an alternative payment choice is worth.
 *
 * The client builds a convoke / delve selection from the lists the enumerator sent it, but that
 * list can go stale (a creature tapped since it was built) and the colour a creature pays is a
 * client-side pick. Before `AlternativePaymentHandler.validateForSpell`, validation priced the
 * cast by whatever the action *claimed* while `execute` silently skipped anything illegal — so
 * an illegal choice either failed as an opaque "Cannot pay mana cost" or, under auto-pay, was
 * quietly covered by tapping extra lands the player never chose. Every case here must be a
 * clean rejection that names the problem and leaves the board untouched.
 */
class AlternativePaymentValidationTest : FunSpec({

    fun createDriver(): GameTestDriver = GameTestDriver().also { it.registerCards(TestCards.all) }

    /** Merrow Skyswimmer ({3}{W/U}{W/U}, convoke) in hand, three lands, a white and a blue creature. */
    data class ConvokeBoard(
        val driver: GameTestDriver,
        val player: com.wingedsheep.sdk.model.EntityId,
        val skyswimmer: com.wingedsheep.sdk.model.EntityId,
        val lands: List<com.wingedsheep.sdk.model.EntityId>,
        val whiteCreature: com.wingedsheep.sdk.model.EntityId,
        val blueCreature: com.wingedsheep.sdk.model.EntityId,
    )

    fun convokeBoard(): ConvokeBoard {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of(
                "Plains" to 10,
                "Island" to 10,
                "Merrow Skyswimmer" to 4,
                "Savannah Lions" to 4,
                "Phantom Warrior" to 4,
            )
        )
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val skyswimmer = driver.putCardInHand(player, "Merrow Skyswimmer")
        val lands = listOf(
            driver.putLandOnBattlefield(player, "Plains"),
            driver.putLandOnBattlefield(player, "Plains"),
            driver.putLandOnBattlefield(player, "Island"),
        )
        val whiteCreature = driver.putCreatureOnBattlefield(player, "Savannah Lions")
        val blueCreature = driver.putCreatureOnBattlefield(player, "Phantom Warrior")
        driver.removeSummoningSickness(whiteCreature)
        driver.removeSummoningSickness(blueCreature)
        return ConvokeBoard(driver, player, skyswimmer, lands, whiteCreature, blueCreature)
    }

    fun ConvokeBoard.cast(payment: AlternativePaymentChoice) = driver.submit(
        CastSpell(
            playerId = player,
            cardId = skyswimmer,
            targets = emptyList(),
            paymentStrategy = PaymentStrategy.AutoPay,
            alternativePayment = payment,
        )
    )

    fun ConvokeBoard.boardUntouched() {
        driver.stackSize shouldBe 0
        driver.isTapped(whiteCreature) shouldBe false
        driver.isTapped(blueCreature) shouldBe false
        lands.none { driver.isTapped(it) } shouldBe true
    }

    context("convoke") {

        test("a creature can't pay a colour it isn't") {
            val board = convokeBoard()
            // Savannah Lions is white; claiming blue would have covered a {W/U} pip anyway
            // (both halves are payable), so the board is the tell, not the price.
            val result = board.cast(
                AlternativePaymentChoice(
                    convokedCreatures = mapOf(board.whiteCreature to ConvokePayment(Color.BLUE))
                )
            )
            result.isSuccess shouldBe false
            result.error!! shouldContain "Savannah Lions can't pay blue mana"
            board.boardUntouched()
        }

        test("a creature that was tapped after the offer is rejected, not silently skipped") {
            val board = convokeBoard()
            board.driver.tapPermanent(board.whiteCreature)
            // Before validation existed this cast *succeeded*: the tapped creature was skipped
            // and auto-pay quietly tapped a land in its place.
            val result = board.cast(
                AlternativePaymentChoice(
                    convokedCreatures = mapOf(
                        board.whiteCreature to ConvokePayment(Color.WHITE),
                        board.blueCreature to ConvokePayment(Color.BLUE),
                    )
                )
            )
            result.isSuccess shouldBe false
            result.error!! shouldContain "Savannah Lions is already tapped"
            board.driver.stackSize shouldBe 0
            board.driver.isTapped(board.blueCreature) shouldBe false
            board.lands.none { board.driver.isTapped(it) } shouldBe true
        }

        test("a permanent that isn't a creature is rejected") {
            val board = convokeBoard()
            val result = board.cast(
                AlternativePaymentChoice(
                    convokedCreatures = mapOf(board.lands.first() to ConvokePayment(null))
                )
            )
            result.isSuccess shouldBe false
            result.error!! shouldContain "isn't a creature"
            board.boardUntouched()
        }

        test("a legal declaration still casts") {
            val board = convokeBoard()
            val result = board.cast(
                AlternativePaymentChoice(
                    convokedCreatures = mapOf(
                        board.whiteCreature to ConvokePayment(Color.WHITE),
                        board.blueCreature to ConvokePayment(Color.BLUE),
                    )
                )
            )
            result.isSuccess shouldBe true
            board.driver.stackSize shouldBe 1
            board.driver.isTapped(board.whiteCreature) shouldBe true
            board.driver.isTapped(board.blueCreature) shouldBe true
        }

        test("a spell without convoke rejects a convoke payment instead of ignoring it") {
            val board = convokeBoard()
            val lions = board.driver.putCardInHand(board.player, "Savannah Lions")
            val result = board.driver.submit(
                CastSpell(
                    playerId = board.player,
                    cardId = lions,
                    targets = emptyList(),
                    paymentStrategy = PaymentStrategy.AutoPay,
                    alternativePayment = AlternativePaymentChoice(
                        convokedCreatures = mapOf(board.blueCreature to ConvokePayment(null))
                    ),
                )
            )
            result.isSuccess shouldBe false
            result.error!! shouldContain "doesn't have convoke"
            board.boardUntouched()
        }
    }

    context("delve") {

        test("a card that isn't in your graveyard can't be exiled for delve") {
            val driver = createDriver()
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20, "Treasure Cruise" to 4, "Savannah Lions" to 4)
            )
            val player = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            val cruise = driver.putCardInHand(player, "Treasure Cruise")
            val inHand = driver.putCardInHand(player, "Savannah Lions")
            val inGraveyard = driver.putCardInGraveyard(player, "Savannah Lions")
            val lands = (1..8).map { driver.putLandOnBattlefield(player, "Island") }

            // Eight Islands pay {7}{U} outright, so before validation this cast succeeded with the
            // hand card "delved" for nothing — the claim was priced, the exile silently skipped.
            val result = driver.submit(
                CastSpell(
                    playerId = player,
                    cardId = cruise,
                    targets = emptyList(),
                    paymentStrategy = PaymentStrategy.AutoPay,
                    alternativePayment = AlternativePaymentChoice(delvedCards = listOf(inGraveyard, inHand)),
                )
            )
            result.isSuccess shouldBe false
            result.error!! shouldContain "isn't in your graveyard"
            driver.stackSize shouldBe 0
            lands.none { driver.isTapped(it) } shouldBe true
        }
    }
})
