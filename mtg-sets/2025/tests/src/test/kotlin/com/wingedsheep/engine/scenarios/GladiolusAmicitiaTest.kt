package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.GladiolusAmicitia
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Gladiolus Amicitia — {4}{R}{G} 6/6 Legendary Creature
 * "Landfall — Whenever a land you control enters, another target creature you
 *  control gets +2/+2 and gains trample until end of turn."
 *
 * Verifies the landfall pump targets ANOTHER creature (the generated draft
 * originally allowed Gladiolus to target itself).
 */
class GladiolusAmicitiaTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GladiolusAmicitia)
        return driver
    }

    test("landfall pumps another creature you control; Gladiolus is not a legal target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gladiolus = driver.putCreatureOnBattlefield(activePlayer, "Gladiolus Amicitia")
        val lions = driver.putCreatureOnBattlefield(activePlayer, "Savannah Lions")

        // Play a land to trigger landfall; the trigger pauses for a target.
        val forest = driver.putCardInHand(activePlayer, "Forest")
        driver.playLand(activePlayer, forest)

        val decision = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        val legal = decision.legalTargets[0] ?: emptyList()
        legal.contains(lions) shouldBe true
        legal.contains(gladiolus) shouldBe false

        driver.submitTargetSelection(activePlayer, listOf(lions))
        driver.bothPass()

        projector.getProjectedPower(driver.state, lions) shouldBe 3
        projector.getProjectedToughness(driver.state, lions) shouldBe 3
    }
})
