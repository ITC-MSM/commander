package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SuspendCardFromHand
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Printed Suspend (CR 702.62a / 116.2f) as a special action is legal "any time you could begin to
 * cast this card" — instant speed for an instant or a card with flash, sorcery speed otherwise.
 * [AncestralVisionScenarioTest] proves the sorcery-speed side (Ancestral Vision is a Sorcery with
 * no flash); this file proves the instant-speed side with synthetic test cards, since no *real*
 * card in the engine's registry currently prints Suspend on an instant or a card with flash.
 *
 * Both cases reuse the same proof: put a spell on the stack first (the caster keeps priority right
 * after casting, but the stack is no longer empty), then confirm the suspend special action still
 * succeeds — the sorcery-speed-only gate (`canPlaySorcerySpeed`, which requires an empty stack)
 * would reject it, so success here demonstrates the instant-speed branch is actually exercised.
 */
class PrintedSuspendTimingTest : FunSpec({

    val testSuspendInstant = card("Test Suspend Bolt") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Suspend 2—{R} (...)\nDraw a card."
        spell {
            effect = Effects.DrawCards(1)
        }
        keywordAbility(KeywordAbility.suspend("{R}", 2))
    }

    val testSuspendFlashCreature = card("Test Suspend Flash Creature") {
        manaCost = "{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        oracleText = "Flash\nSuspend 2—{G} (...)"
        keywords(Keyword.FLASH)
        keywordAbility(KeywordAbility.suspend("{G}", 2))
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(testSuspendInstant, testSuspendFlashCreature))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        return driver
    }

    test("an instant with printed suspend can be suspended with a non-empty stack (instant speed)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val card = driver.putCardInHand(me, "Test Suspend Bolt")
        driver.giveMana(me, Color.RED, 1)

        // Occupy the stack first: cast Lightning Bolt, which leaves the caster with priority but
        // a non-empty stack — the condition sorcery-speed suspend (Ancestral Vision) is rejected
        // under, per AncestralVisionScenarioTest's "can only be taken at sorcery speed" test.
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, listOf(opponent)).isSuccess shouldBe true
        driver.state.stack.isEmpty() shouldBe false
        driver.state.priorityPlayerId shouldBe me

        driver.submit(SuspendCardFromHand(me, card)).isSuccess shouldBe true
        driver.getExile(me).contains(card) shouldBe true
    }

    test("a non-instant card with printed flash and suspend can also be suspended with a non-empty stack") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val card = driver.putCardInHand(me, "Test Suspend Flash Creature")
        driver.giveMana(me, Color.GREEN, 1)

        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, listOf(opponent)).isSuccess shouldBe true
        driver.state.stack.isEmpty() shouldBe false
        driver.state.priorityPlayerId shouldBe me

        driver.submit(SuspendCardFromHand(me, card)).isSuccess shouldBe true
        driver.getExile(me).contains(card) shouldBe true
    }
})
