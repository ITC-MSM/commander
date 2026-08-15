package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.ServantOfTheStinger
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Servant of the Stinger (OTJ #105).
 *
 * "{1}{B} Creature — Human Warlock 1/3. Deathtouch.
 *  Whenever this creature deals combat damage to a player, if you've committed a crime this turn,
 *  you may sacrifice this creature. If you do, search your library for a card, put it into your
 *  hand, then shuffle."
 *
 * The crime clause is an intervening-if. Verifies: with a crime committed, the player may
 * sacrifice on combat damage and tutor; without a crime, the ability does nothing.
 */
class ServantOfTheStingerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(ServantOfTheStinger))
        return driver
    }

    test("with a crime committed, may sacrifice on combat damage and tutor a card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30, "Forest" to 10), startingLife = 20)

        val attacker = driver.player1
        val defender = driver.player2

        val servant = driver.putCreatureOnBattlefield(attacker, "Servant of the Stinger")
        driver.removeSummoningSickness(servant)

        // Mark that the active player committed a crime this turn (intervening-if gate).
        driver.replaceState(driver.state.copy(playersWhoCommittedCrimeThisTurn = setOf(attacker)))

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        // A specific tutor target on top of the library.
        val target = driver.putCardOnTopOfLibrary(attacker, "Forest")
        val handBefore = driver.getHandSize(attacker)

        driver.declareAttackers(attacker, listOf(servant), defender)
        driver.bothPass()
        driver.declareNoBlockers(defender)
        driver.bothPass()

        // Combat damage dealt -> trigger goes on the stack; resolve it.
        driver.bothPass()

        // "You may sacrifice this creature." -> yes.
        driver.submitYesNo(attacker, true)
        // Search your library for a card -> pick the Forest.
        driver.submitCardSelection(attacker, listOf(target))

        // Servant was sacrificed.
        driver.state.getZone(com.wingedsheep.engine.state.ZoneKey(attacker, Zone.BATTLEFIELD))
            .contains(servant) shouldBe false
        // The tutored card is in hand.
        driver.getHand(attacker).contains(target) shouldBe true
        driver.getHandSize(attacker) shouldBe handBefore + 1
        // Defender took 1 combat damage.
        driver.assertLifeTotal(defender, 19)
    }

    test("without a crime, the ability does nothing on combat damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30, "Forest" to 10), startingLife = 20)

        val attacker = driver.player1
        val defender = driver.player2

        val servant = driver.putCreatureOnBattlefield(attacker, "Servant of the Stinger")
        driver.removeSummoningSickness(servant)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val handBefore = driver.getHandSize(attacker)

        driver.declareAttackers(attacker, listOf(servant), defender)
        driver.bothPass()
        driver.declareNoBlockers(defender)
        driver.bothPass()

        // Combat damage dealt; no crime committed, so the intervening-if fails — no prompt.
        driver.bothPass()

        // Servant survives; no tutor.
        driver.state.getZone(com.wingedsheep.engine.state.ZoneKey(attacker, Zone.BATTLEFIELD))
            .contains(servant) shouldBe true
        driver.getHandSize(attacker) shouldBe handBefore
        driver.assertLifeTotal(defender, 19)
    }
})
