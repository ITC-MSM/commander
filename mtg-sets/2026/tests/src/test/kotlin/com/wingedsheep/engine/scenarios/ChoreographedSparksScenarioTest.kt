package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.ChoreographedSparks
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Choreographed Sparks {R}{R} Instant (SOS canonical).
 *
 * This spell can't be copied.
 * Choose one or both —
 * • Copy target instant or sorcery spell you control. You may choose new targets for the copy.
 * • Copy target creature spell you control. The copy gains haste and
 *   "At the beginning of the end step, sacrifice this token."
 */
class ChoreographedSparksScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ChoreographedSparks))
        return driver
    }

    test("mode 0 — copies your instant/sorcery spell on the stack") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Lightning Bolt at the opponent; it sits on the stack.
        driver.giveMana(me, Color.RED, 1)
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.submit(
            CastSpell(playerId = me, cardId = bolt, targets = listOf(ChosenTarget.Player(opp)))
        ).error shouldBe null

        // In response, cast Choreographed Sparks (mode 0) copying the Bolt.
        driver.giveMana(me, Color.RED, 2)
        val sparks = driver.putCardInHand(me, "Choreographed Sparks")
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = sparks,
                targets = listOf(ChosenTarget.Spell(bolt)),
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(bolt)))
            )
        ).error shouldBe null

        // Sparks resolves first and copies the Bolt, pausing to choose the copy's target.
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 30) {
            val pending = driver.pendingDecision
            if (pending is ChooseTargetsDecision) {
                driver.submitDecision(me, TargetsResponse(pending.id, mapOf(0 to listOf(opp))))
            } else {
                driver.bothPass()
            }
        }

        // Bolt (3) + copy (3) = 6 damage to the opponent.
        driver.getLifeTotal(opp) shouldBe 14
    }

    test("mode 1 — copies your creature spell into a hasty token that's sacrificed at end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast a creature spell (Centaur Courser {2}{G}, 3/3); it sits on the stack.
        driver.giveMana(me, Color.GREEN, 3) // {G} + {2} generic
        val courser = driver.putCardInHand(me, "Centaur Courser")
        driver.submit(CastSpell(playerId = me, cardId = courser)).error shouldBe null

        // In response, Choreographed Sparks (mode 1) copies the creature spell.
        driver.giveMana(me, Color.RED, 2)
        val sparks = driver.putCardInHand(me, "Choreographed Sparks")
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = sparks,
                targets = listOf(ChosenTarget.Spell(courser)),
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(courser)))
            )
        ).error shouldBe null

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 30) driver.bothPass()

        // Two Centaur Coursers now exist: the real one + the token copy.
        val coursers = driver.getPermanents(me).filter {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == "Centaur Courser"
        }
        coursers.size shouldBe 2

        // The token copy has haste (granted by the rider).
        val tokenCopy = coursers.first { driver.state.getEntity(it)?.has<TokenComponent>() == true }
        driver.state.projectedState.hasKeyword(tokenCopy, Keyword.HASTE) shouldBe true

        // Advance to this end step — the token is sacrificed.
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        val coursersAfter = driver.getPermanents(me).filter {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == "Centaur Courser"
        }
        // The real Courser survives; the token is gone.
        coursersAfter.size shouldBe 1
    }
})
