package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rise of the Varmints (OTJ #179) — {3}{G} Sorcery, Plot {2}{G}.
 *
 *   "Create X 2/1 green Varmint creature tokens, where X is the number of creature cards in your
 *    graveyard."
 *
 * Verifies the resolution-time count (DynamicAmount.Count over the graveyard, creature filter):
 * only creature cards in the graveyard count, and each token is a 2/1 green Varmint.
 */
class RiseOfTheVarmintsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.varmints(playerId: com.wingedsheep.sdk.model.EntityId) =
        getCreatures(playerId).filter {
            state.getEntity(it)?.get<CardComponent>()?.name == "Varmint Token"
        }

    test("creates one 2/1 green Varmint per creature card in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        val projector = StateProjector()

        // Three creature cards + one non-creature card in the graveyard. Only creatures count.
        driver.putCardInGraveyard(player, "Grizzly Bears")
        driver.putCardInGraveyard(player, "Grizzly Bears")
        driver.putCardInGraveyard(player, "Grizzly Bears")
        driver.putCardInGraveyard(player, "Lightning Bolt")

        // {3}{G} → 4 mana.
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 3)

        val rise = driver.putCardInHand(player, "Rise of the Varmints")
        val cast = driver.castSpell(player, rise)
        cast.error shouldBe null
        driver.bothPass()

        val tokens = driver.varmints(player)
        tokens.size shouldBe 3

        val projected = projector.project(driver.state)
        for (token in tokens) {
            val card = driver.state.getEntity(token)!!.get<CardComponent>()!!
            card.colors shouldBe setOf(Color.GREEN)
            projected.getPower(token) shouldBe 2
            projected.getToughness(token) shouldBe 1
        }
    }

    test("empty graveyard creates no tokens") {
        val driver = createDriver()
        val player = driver.player1

        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 3)

        val rise = driver.putCardInHand(player, "Rise of the Varmints")
        val cast = driver.castSpell(player, rise)
        cast.error shouldBe null
        driver.bothPass()

        driver.varmints(player).size shouldBe 0
    }
})
