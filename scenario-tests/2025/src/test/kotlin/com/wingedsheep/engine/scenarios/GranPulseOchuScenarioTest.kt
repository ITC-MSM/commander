package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.GranPulseOchu
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Gran Pulse Ochu (FIN #189).
 *
 * Gran Pulse Ochu {G} Creature — Plant Beast 1/1
 * Deathtouch
 * {8}: Until end of turn, this creature gets +1/+1 for each permanent card in your graveyard.
 */
class GranPulseOchuScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GranPulseOchu)
        return driver
    }

    test("activated ability pumps +1/+1 for each permanent card in your graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ochu = driver.putCreatureOnBattlefield(active, "Gran Pulse Ochu")

        // Three permanent cards in the graveyard (creature cards are permanent cards).
        driver.putCardInGraveyard(active, "Grizzly Bears")
        driver.putCardInGraveyard(active, "Grizzly Bears")
        driver.putCardInGraveyard(active, "Grizzly Bears")

        // Base stats before activation.
        projector.project(driver.state).getPower(ochu) shouldBe 1

        driver.giveColorlessMana(active, 8)
        val abilityId = GranPulseOchu.script.activatedAbilities[0].id
        driver.submit(ActivateAbility(playerId = active, sourceId = ochu, abilityId = abilityId))
        driver.bothPass() // resolve the activated ability

        val projected = projector.project(driver.state)
        projected.getPower(ochu) shouldBe 4
        projected.getToughness(ochu) shouldBe 4
    }
})
