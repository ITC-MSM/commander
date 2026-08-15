package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rix.cards.KitesailCorsair
import com.wingedsheep.mtg.sets.definitions.xln.cards.PiratesCutlass
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pirate's Cutlass — {3} Artifact — Equipment
 *
 * "When this Equipment enters, attach it to target Pirate you control.
 *  Equipped creature gets +2/+1.
 *  Equip {2}"
 */
class PiratesCutlassScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(PiratesCutlass, KitesailCorsair))
        return driver
    }

    test("ETB attaches to a target Pirate, granting +2/+1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val corsair = driver.putCreatureOnBattlefield(me, "Kitesail Corsair") // 2/1 Pirate

        projector.getProjectedPower(driver.state, corsair) shouldBe 2
        projector.getProjectedToughness(driver.state, corsair) shouldBe 1

        val cutlass = driver.putCardInHand(me, "Pirate's Cutlass")
        driver.giveColorlessMana(me, 3)
        driver.castSpell(me, cutlass)
        driver.bothPass() // resolve the equipment spell -> it enters -> ETB trigger on stack
        driver.bothPass() // resolve ETB trigger -> pauses for target selection

        driver.submitTargetSelection(me, listOf(corsair))
        driver.bothPass()

        // Attached to the Pirate, with +2/+1.
        val cutlassId = driver.findPermanent(me, "Pirate's Cutlass")!!
        driver.state.getEntity(cutlassId)?.get<AttachedToComponent>()?.targetId shouldBe corsair
        projector.getProjectedPower(driver.state, corsair) shouldBe 4
        projector.getProjectedToughness(driver.state, corsair) shouldBe 2
    }
})
