package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rix.cards.KitesailCorsair
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Kitesail Corsair — {1}{U} Creature — Human Pirate 2/1
 *
 * "This creature has flying as long as it's attacking."
 */
class KitesailCorsairScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(KitesailCorsair)
        return driver
    }

    test("gains flying only while attacking") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val corsair = driver.putCreatureOnBattlefield(me, "Kitesail Corsair")
        driver.removeSummoningSickness(corsair)

        // Not attacking yet → no flying.
        projector.project(driver.state).hasKeyword(corsair, Keyword.FLYING) shouldBe false

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(corsair), opponent)

        // While attacking → has flying.
        projector.project(driver.state).hasKeyword(corsair, Keyword.FLYING) shouldBe true
    }
})
