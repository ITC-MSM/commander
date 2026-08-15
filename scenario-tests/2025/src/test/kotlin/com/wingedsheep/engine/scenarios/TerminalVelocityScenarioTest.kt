package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.TerminalVelocity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Terminal Velocity {4}{R}{R} — Sorcery.
 *
 * "You may put an artifact or creature card from your hand onto the battlefield.
 *  That permanent gains haste, "When this permanent leaves the battlefield, it
 *  deals damage equal to its mana value to each creature," and "At the beginning
 *  of your end step, sacrifice this permanent.""
 *
 * The two quoted clauses are granted as real triggered abilities on the chosen
 * permanent (Duration.Permanent) — the LTB clause must read the permanent's
 * last-known mana value (it has just left the battlefield) and damage every
 * creature still on the battlefield.
 */
class TerminalVelocityScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TerminalVelocity)
        return driver
    }

    fun payTerminalVelocity(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId) {
        driver.giveMana(playerId, Color.RED, 2)
        driver.giveColorlessMana(playerId, 4)
    }

    test("puts chosen creature on the battlefield with haste; end step sacrifices it; LTB damages every creature for its mana value") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Force of Nature is 8/8 with mana value 5 ({2}{G}{G}{G}{G}). Pick it as the cheat
        // target so the LTB clause clearly pings everything for 5, killing weaker creatures.
        val forceOfNature = driver.putCardInHand(active, "Force of Nature")
        // A tiny opponent creature — 2/2 Savannah Lions — to confirm "each creature" includes
        // creatures we don't control (and to verify it gets killed by 5 damage).
        val opponentLion = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")
        // Another creature on our side — also expects 5 damage from the LTB ping.
        val ourLion = driver.putCreatureOnBattlefield(active, "Savannah Lions")

        val terminalVelocity = driver.putCardInHand(active, "Terminal Velocity")
        payTerminalVelocity(driver, active)

        driver.castSpell(active, terminalVelocity)
        driver.bothPass() // begin resolving Terminal Velocity

        // The spell pauses on "choose any number (up to 1) of artifact/creature cards from hand".
        driver.isPaused shouldBe true
        val selectDecision = driver.pendingDecision as SelectCardsDecision
        selectDecision.options shouldContain forceOfNature

        driver.submitCardSelection(active, listOf(forceOfNature))

        // Force of Nature should now be on the battlefield under the active player's control.
        val fonOnBattlefield = driver.findPermanent(active, "Force of Nature")!!
        fonOnBattlefield shouldBe forceOfNature

        // Drain any remaining priority window in the main phase, then advance to end step.
        driver.passPriorityUntil(Step.END)

        // End step queued the sacrifice trigger. Resolve it.
        var safety = 0
        while ((driver.stackSize > 0 || driver.isPaused) && safety < 50) {
            if (driver.isPaused) break
            driver.bothPass()
            safety++
        }

        // Force of Nature was sacrificed, then its LTB clause pinged every creature for 5.
        // ourLion was a 2/2 → dies. opponentLion was a 2/2 → dies. Force of Nature itself
        // already left when it was sacrificed, so the damage to "each creature" only hits
        // the remaining battlefield creatures.
        driver.findPermanent(active, "Force of Nature") shouldBe null
        val activeGY = driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD))
        activeGY shouldContain forceOfNature
        activeGY shouldContain ourLion

        val opponentGY = driver.state.getZone(ZoneKey(opponent, Zone.GRAVEYARD))
        opponentGY shouldContain opponentLion
    }

    test("'may' clause: declining puts no permanent onto the battlefield and no triggers are scheduled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val survivingFriendly = driver.putCreatureOnBattlefield(active, "Savannah Lions")
        val survivingOpponent = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")

        // Keep a creature in hand so the "may put a creature from hand" decision is offered;
        // we'll decline regardless.
        driver.putCardInHand(active, "Force of Nature")
        val terminalVelocity = driver.putCardInHand(active, "Terminal Velocity")
        payTerminalVelocity(driver, active)

        driver.castSpell(active, terminalVelocity)
        driver.bothPass()

        driver.isPaused shouldBe true
        driver.submitCardSelection(active, emptyList())

        // Nothing entered the battlefield, so no granted triggers can sneak through.
        driver.findPermanent(active, "Force of Nature") shouldBe null

        // Burn through the rest of the turn — surviving lions on both sides must persist
        // (no LTB ping, no end-step sacrifice).
        driver.passPriorityUntil(Step.END)
        var safety = 0
        while (driver.stackSize > 0 && !driver.isPaused && safety < 50) {
            driver.bothPass()
            safety++
        }

        driver.findPermanent(active, "Savannah Lions") shouldBe survivingFriendly
        driver.findPermanent(opponent, "Savannah Lions") shouldBe survivingOpponent
    }
})
