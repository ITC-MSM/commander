package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.CloudOfDarkness
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cloud of Darkness — {2}{B}{G}{G} 3/3 Legendary Creature
 * "Particle Beam — When Cloud of Darkness enters, target creature an opponent
 *  controls gets -X/-X until end of turn, where X is the number of permanent
 *  cards in your graveyard."
 *
 * Verifies X counts PERMANENT cards in your graveyard (the generated draft
 * originally counted creatures you control on the battlefield).
 */
class CloudOfDarknessTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CloudOfDarkness)
        return driver
    }

    test("gives -X/-X equal to permanent cards in your graveyard, ignoring instants/sorceries") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Forest" to 20), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Graveyard: 2 permanent cards (creatures) + 1 nonpermanent (instant). X must be 2.
        driver.putCardInGraveyard(activePlayer, "Savannah Lions")
        driver.putCardInGraveyard(activePlayer, "Centaur Courser")
        driver.putCardInGraveyard(activePlayer, "Lightning Bolt")

        // Opponent has a 3/3 to be targeted.
        val target = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        projector.getProjectedToughness(driver.state, target) shouldBe 3

        val cloud = driver.putCardInHand(activePlayer, "Cloud of Darkness")
        driver.giveMana(activePlayer, Color.BLACK, 1)
        driver.giveMana(activePlayer, Color.GREEN, 2)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, cloud)
        driver.bothPass() // resolve the creature spell -> ETB trigger goes on stack

        // ETB trigger asks for a target.
        driver.submitTargetSelection(activePlayer, listOf(target))
        driver.bothPass()

        // -2/-2: a 3/3 becomes a 1/1.
        projector.getProjectedPower(driver.state, target) shouldBe 1
        projector.getProjectedToughness(driver.state, target) shouldBe 1
    }
})
