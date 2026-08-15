package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.StrongboxRaider
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Strongbox Raider — {2}{R}{R} Orc Pirate 5/2
 *
 * "Raid — When this creature enters, if you attacked this turn, exile the top two cards of your
 * library. Choose one of them. Until the end of your next turn, you may play that card."
 *
 * Proves the Raid intervening-"if" gates the impulse-exile pipeline: when the controller attacked
 * this turn both top cards are exiled, the player chooses one, and only the chosen card gets
 * may-play permission; with no attack the ETB does nothing.
 */
class StrongboxRaiderScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(StrongboxRaider))
        return driver
    }

    test("raid active: exiles top two, chosen one is playable, the other is not") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // Attack this turn to satisfy Raid.
        val goblin = driver.putCreatureOnBattlefield(me, "Goblin Guide")
        driver.removeSummoningSickness(goblin)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(goblin), defendingPlayer = opp).error shouldBe null
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Seed the top two cards of the library.
        val second = driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        val first = driver.putCardOnTopOfLibrary(me, "Mountain")
        val seeded = setOf(first, second)

        driver.giveMana(me, Color.RED, 4)
        val raider = driver.putCardInHand(me, "Strongbox Raider")
        driver.castSpell(me, raider).error shouldBe null

        var chosen: EntityId? = null
        var safety = 0
        while (safety < 40) {
            val pd = driver.state.pendingDecision
            when {
                pd is SelectCardsDecision -> {
                    val pick = pd.options.first()
                    chosen = pick
                    driver.submitCardSelection(pd.playerId, listOf(pick))
                }
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
            safety++
        }

        val chosenCard = chosen
        (chosenCard != null) shouldBe true
        (chosenCard in seeded) shouldBe true

        // Both cards are exiled.
        driver.getExile(me).toSet() shouldBe seeded

        // Only the chosen card is playable.
        driver.state.mayPlayPermissions.any { chosenCard in it.cardIds } shouldBe true
        val notChosen = seeded.single { it != chosenCard }
        driver.state.mayPlayPermissions.any { notChosen in it.cardIds } shouldBe false
    }

    test("no attack this turn: raid ETB does nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Mountain")

        driver.giveMana(me, Color.RED, 4)
        val raider = driver.putCardInHand(me, "Strongbox Raider")
        driver.castSpell(me, raider).error shouldBe null

        var safety = 0
        while (driver.stackSize > 0 && safety < 20) {
            (driver.state.pendingDecision is SelectCardsDecision) shouldBe false
            driver.bothPass()
            safety++
        }

        driver.getExile(me).isEmpty() shouldBe true
    }
})
