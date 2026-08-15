package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.RingBearerComponent
import com.wingedsheep.engine.state.components.player.TheRingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.BirthdayEscape
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Birthday Escape ({U} Sorcery): "Draw a card. The Ring tempts you."
 */
class BirthdayEscapeTest : FunSpec({

    val Bear = CardDefinition.creature("Test Bear", ManaCost.parse("{2}"), emptySet(), 2, 2)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BirthdayEscape, Bear))
        return driver
    }

    test("draws a card and tempts the caster with the Ring") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear = driver.putCreatureOnBattlefield(active, "Test Bear")
        val handBefore = driver.getHandSize(active)

        val spell = driver.putCardInHand(active, "Birthday Escape")
        driver.giveMana(active, com.wingedsheep.sdk.core.Color.BLUE, 1)
        driver.castSpell(active, spell)
        driver.bothPass() // resolve: draw a card, then pause to choose a Ring-bearer

        val decision = driver.pendingDecision as SelectCardsDecision
        driver.submitDecision(active, CardsSelectedResponse(decision.id, listOf(bear)))

        // Drew exactly one card (Birthday Escape itself left the hand to the stack/graveyard).
        driver.getHandSize(active) shouldBe handBefore + 1
        driver.state.getEntity(active)?.get<TheRingComponent>()?.temptCount shouldBe 1
        driver.state.getEntity(bear)?.get<RingBearerComponent>()?.ownerId shouldBe active
    }
})
