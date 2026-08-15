package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m20.cards.DevoutDecree
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Devout Decree (M20) — {1}{W} Sorcery
 *
 * "Exile target creature or planeswalker that's black or red. Scry 1."
 */
class DevoutDecreeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DevoutDecree)
        return driver
    }

    test("exiles a black creature and then scries") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A black creature the opponent controls — a legal target ("black or red").
        val victim = driver.putCreatureOnBattlefield(opponent, "Black Creature")
        driver.findPermanent(opponent, "Black Creature") shouldNotBe null

        // Ensure Scry 1 has a card to look at.
        driver.putCardOnTopOfLibrary(you, "Plains")

        val decree = driver.putCardInHand(you, "Devout Decree")
        driver.giveMana(you, Color.WHITE, 2)
        val cast = driver.castSpell(you, decree, targets = listOf(victim))
        cast.isSuccess shouldBe true

        driver.bothPass() // resolve Devout Decree: exile the target, then pause for Scry 1
        // Resolve the Scry decision (SelectCardsDecision, min 0 → keep on top).
        while (driver.pendingDecision != null) {
            driver.autoResolveDecision()
        }

        // The black creature is exiled (exile is owner-keyed → owner is the opponent).
        driver.findPermanent(opponent, "Black Creature") shouldBe null
        driver.getExileCardNames(opponent).contains("Black Creature") shouldBe true

        // Devout Decree itself is in its owner's graveyard.
        driver.getGraveyardCardNames(you).contains("Devout Decree") shouldBe true
    }

    test("a white creature is not a legal target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Savannah Lions is white — neither black nor red, so it can't be targeted.
        val whiteCreature = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")

        val decree = driver.putCardInHand(you, "Devout Decree")
        driver.giveMana(you, Color.WHITE, 2)
        val cast = driver.castSpell(you, decree, targets = listOf(whiteCreature))

        // Casting with an illegal target must fail; the white creature stays on the battlefield.
        cast.isSuccess shouldBe false
        driver.findPermanent(opponent, "Savannah Lions") shouldNotBe null
    }
})
