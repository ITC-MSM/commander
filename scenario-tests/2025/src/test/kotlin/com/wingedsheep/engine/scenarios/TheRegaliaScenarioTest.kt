package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheRegalia
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Regalia — {4} Legendary Artifact — Vehicle, 4/4, Haste, Crew 1.
 *
 * "Whenever The Regalia attacks, reveal cards from the top of your library until you reveal a land
 *  card. Put that card onto the battlefield tapped and the rest on the bottom of your library in a
 *  random order."
 *
 * Exercises the reveal-until-land attack trigger: a nonland is revealed, then the first land is put
 * onto the battlefield tapped while the nonland goes to the bottom of the library.
 */
class TheRegaliaScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TheRegalia)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("attacking reveals until a land, putting it onto the battlefield tapped") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        // The Regalia and a crewer present since before the turn (no summoning sickness).
        val regalia = driver.putPermanentOnBattlefield(me, "The Regalia")
        driver.removeSummoningSickness(regalia)
        val crewer = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.removeSummoningSickness(crewer)

        // Stack the library: top is a nonland, the land sits just beneath it.
        driver.putCardOnTopOfLibrary(me, "Forest")          // becomes 2nd from top
        driver.putCardOnTopOfLibrary(me, "Centaur Courser") // top (nonland)

        val landsBefore = driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD))
            .count { driver.getCardName(it) == "Forest" }

        // Crew The Regalia (Crew 1, Courser power 3 covers it), then attack.
        driver.submitSuccess(CrewVehicle(me, regalia, listOf(crewer)))
        driver.bothPass() // resolve the crew activation
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.submitSuccess(DeclareAttackers(me, mapOf(regalia to opponent)))

        // Resolve the attack trigger.
        if (driver.stackSize > 0) driver.bothPass()

        // The Forest is on the battlefield, tapped; the nonland went to the bottom of the library.
        val forest = driver.findPermanent(me, "Forest")!!
        driver.isTapped(forest) shouldBe true

        val landsAfter = driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD))
            .count { driver.getCardName(it) == "Forest" }
        landsAfter shouldBe landsBefore + 1

        // The revealed nonland is back in the library (bottom), not exiled or in hand.
        driver.state.getZone(ZoneKey(me, Zone.LIBRARY)).any { driver.getCardName(it) == "Centaur Courser" } shouldBe true
    }
})
