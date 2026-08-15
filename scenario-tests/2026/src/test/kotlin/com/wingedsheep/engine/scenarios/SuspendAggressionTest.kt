package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.SuspendAggression
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Suspend Aggression {1}{R}{W} — Instant.
 * "Exile target nonland permanent and the top card of your library. For each of those cards, its
 *  owner may play it until the end of their next turn."
 *
 * Exercises the new `GrantMayPlayFromExile(ownerControls = true)`: the targeted opponent's
 * permanent goes to exile and its *owner* (the opponent) — not the caster — may replay it, while
 * the caster may play their own exiled top library card.
 */
class SuspendAggressionTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SuspendAggression))
        return driver
    }

    fun mayPlay(driver: GameTestDriver, player: EntityId, cardId: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        return enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
            .any { it.actionType == "CastSpell" && (it.action as? CastSpell)?.cardId == cardId }
    }

    test("exiles target permanent and own top card; each card's owner gets the may-play permission") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls a creature; it's their card.
        val oppCreature = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // Stack a known card on top of my library to verify it gets exiled and becomes playable.
        val myTop = driver.putCardOnTopOfLibrary(me, "Lightning Bolt")

        val spell = driver.putCardInHand(me, "Suspend Aggression")
        // {1}{R}{W}: a Plains for {W}, two Mountains for {R} and {1}.
        repeat(2) { driver.putLandOnBattlefield(me, "Mountain") }
        repeat(2) { driver.putLandOnBattlefield(me, "Plains") }

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(oppCreature)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // Both cards are in exile, each in their owner's exile pile.
        driver.state.getZone(ZoneKey(opp, Zone.EXILE)) shouldContain oppCreature
        driver.state.getZone(ZoneKey(me, Zone.EXILE)) shouldContain myTop

        // The targeted permanent's OWNER (the opponent) may play it; the caster may NOT.
        mayPlay(driver, opp, oppCreature) shouldBe true
        mayPlay(driver, me, oppCreature) shouldBe false

        // The caster's own exiled top card is playable by the caster.
        mayPlay(driver, me, myTop) shouldBe true
    }
})
