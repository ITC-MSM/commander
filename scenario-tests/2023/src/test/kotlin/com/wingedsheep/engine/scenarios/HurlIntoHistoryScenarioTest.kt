package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.HurlIntoHistory
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Hurl into History {3}{U}{U} — "Counter target artifact or creature spell. Discover X, where X is
 * that spell's mana value." Proves the discover threshold is read from the *countered* spell's mana
 * value: countering a mana-value-3 creature spell must discover 3 (i.e. a nonland card with mana
 * value ≤ 3 is a legal hit), not 0.
 */
class HurlIntoHistoryScenarioTest : FunSpec({

    // A mana-value-3 creature spell — a legal Hurl target.
    val ogre = card("Test Ogre") {
        manaCost = "{2}{R}"
        typeLine = "Creature — Ogre"
        power = 3
        toughness = 3
    }
    // A mana-value-3 nonland card to sit on top of the Hurl caster's library: discovered only if X >= 3.
    val relic = card("Test Relic") {
        manaCost = "{3}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }

    test("counters a mana-value-3 creature spell and discovers 3") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ogre)
        driver.registerCard(relic)
        driver.registerCard(HurlIntoHistory)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!       // casts the creature on their main phase
        val me = driver.getOpponent(caster)      // casts Hurl in response

        // Top of my library: a mana-value-3 nonland — discovered iff the threshold is >= 3.
        driver.putCardOnTopOfLibrary(me, "Test Relic")

        // Active player casts the Ogre (mana value 3).
        val ogreCard = driver.putCardInHand(caster, "Test Ogre")
        driver.giveMana(caster, Color.RED, 1)
        driver.giveColorlessMana(caster, 2)
        driver.castSpell(caster, ogreCard)
        val ogreOnStack = driver.getTopOfStack()!!
        driver.passPriority(caster)

        // I respond with Hurl into History targeting the Ogre spell.
        val hurl = driver.putCardInHand(me, "Hurl into History")
        driver.giveMana(me, Color.BLUE, 2)
        driver.giveColorlessMana(me, 3)
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = hurl,
                targets = listOf(ChosenTarget.Spell(ogreOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true

        driver.bothPass() // Hurl resolves: counter the Ogre, then Discover 3.

        // The Ogre was countered.
        driver.getGraveyardCardNames(caster) shouldContain "Test Ogre"

        // Discover 3 found the mana-value-3 relic on top → cast-or-hand decision for me.
        // (If the threshold had read 0, the relic would be skipped and the discover would whiff.)
        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
    }
})
