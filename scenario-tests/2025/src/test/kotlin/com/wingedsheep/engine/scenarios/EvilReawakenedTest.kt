package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.EvilReawakened
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Evil Reawakened — {4}{B} Sorcery
 * "Return target creature card from your graveyard to the battlefield with two
 *  additional +1/+1 counters on it."
 *
 * Verifies the reanimated creature returns AND enters with two +1/+1 counters
 * (the generated draft originally dropped the counters).
 */
class EvilReawakenedTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(EvilReawakened)
        return driver
    }

    test("returns target creature card with two additional +1/+1 counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Savannah Lions (1/1) waiting in the graveyard.
        val lionsInGy = driver.putCardInGraveyard(activePlayer, "Savannah Lions")

        val spell = driver.putCardInHand(activePlayer, "Evil Reawakened")
        driver.giveMana(activePlayer, Color.BLACK, 5)

        driver.castSpellWithTargets(
            activePlayer,
            spell,
            listOf(ChosenTarget.Card(lionsInGy, activePlayer, Zone.GRAVEYARD))
        )
        driver.bothPass()

        // Lions is back on the battlefield as a 1/1 base with two +1/+1 counters = 3/3.
        val lions = driver.findPermanent(activePlayer, "Savannah Lions")
        lions shouldNotBe null
        projector.getProjectedPower(driver.state, lions!!) shouldBe 3
        projector.getProjectedToughness(driver.state, lions) shouldBe 3
        driver.getGraveyardCardNames(activePlayer).contains("Savannah Lions") shouldBe false
    }
})
