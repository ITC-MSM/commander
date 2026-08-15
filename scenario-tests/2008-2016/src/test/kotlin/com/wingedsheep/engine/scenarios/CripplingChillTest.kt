package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.KhansOfTarkirSet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Crippling Chill.
 *
 * Crippling Chill: {2}{U}
 * Instant
 * Tap target creature. It doesn't untap during its controller's next untap step.
 * Draw a card.
 */
class CripplingChillTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + KhansOfTarkirSet.cards)
        return driver
    }

    test("taps target creature and prevents untap on controller's next untap step") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put Crippling Chill in player 1's hand
        val chill = driver.putCardInHand(player1, "Crippling Chill")

        // Put a creature on the battlefield for player 2
        val creature = driver.putCreatureOnBattlefield(player2, "Savannah Lions")
        driver.removeSummoningSickness(creature)

        // Give player 1 enough mana to cast (2U)
        driver.giveMana(player1, Color.BLUE, 3)

        // Cast Crippling Chill targeting the creature
        driver.castSpell(player1, chill, targets = listOf(creature))

        // Resolve (both pass)
        driver.bothPass()

        // Creature should be tapped
        driver.isTapped(creature) shouldBe true

        // Advance to player 2's untap step (next turn)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player2

        // Creature should STILL be tapped (DOESNT_UNTAP prevented untapping)
        driver.isTapped(creature) shouldBe true
    }

    test("creature untaps normally on subsequent untap step") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val chill = driver.putCardInHand(player1, "Crippling Chill")
        val creature = driver.putCreatureOnBattlefield(player2, "Savannah Lions")
        driver.removeSummoningSickness(creature)

        driver.giveMana(player1, Color.BLUE, 3)
        driver.castSpell(player1, chill, targets = listOf(creature))
        driver.bothPass()

        driver.isTapped(creature) shouldBe true

        // Advance to player 2's upkeep (creature stays tapped)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player2
        driver.isTapped(creature) shouldBe true

        // Advance through P2's turn, to P1's upkeep
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player1

        // Advance through P1's turn, to P2's upkeep
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player2

        // Creature should now be untapped (effect expired)
        driver.isTapped(creature) shouldBe false
    }

    test("prevents untap when cast on opponent's turn") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)

        // Put creature on P2's battlefield before advancing
        val creature = driver.putCreatureOnBattlefield(player2, "Savannah Lions")
        driver.removeSummoningSickness(creature)

        // Advance past P1's turn to P2's upkeep
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200) // P1's upkeep
        driver.activePlayer shouldBe player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200) // P2's upkeep
        driver.activePlayer shouldBe player2

        // P2 passes priority in upkeep, P1 gets priority and casts instant
        val chill = driver.putCardInHand(player1, "Crippling Chill")
        driver.passPriority(player2)
        driver.giveMana(player1, Color.BLUE, 3)
        driver.castSpell(player1, chill, targets = listOf(creature))

        // Resolve
        driver.bothPass()

        // Creature should be tapped
        driver.isTapped(creature) shouldBe true

        // Floating effect should exist
        driver.state.floatingEffects.any {
            it.duration is Duration.UntilAfterAffectedControllersNextUntap
        } shouldBe true

        // Advance through rest of P2's turn and P1's turn to P2's next upkeep
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe player2

        // Creature should STILL be tapped (DOESNT_UNTAP should prevent untapping
        // during its controller's next untap step)
        driver.isTapped(creature) shouldBe true
    }

    test("draws a card after tapping") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val chill = driver.putCardInHand(player1, "Crippling Chill")
        val creature = driver.putCreatureOnBattlefield(player2, "Savannah Lions")

        val handSizeBefore = driver.getHand(player1).size
        driver.giveMana(player1, Color.BLUE, 3)
        driver.castSpell(player1, chill, targets = listOf(creature))
        driver.bothPass()

        // Hand size should be same as before casting (cast 1, drew 1)
        // Actually: before casting we had chill in hand. After cast, chill is gone (-1) but we drew (+1)
        // So net change from handSizeBefore: -1 (chill cast) + 1 (draw) = 0
        driver.getHand(player1).size shouldBe handSizeBefore - 1 + 1
    }
})
