package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.state.components.battlefield.SaddledComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bridled Bighorn (OTJ #7) — {3}{W} Sheep Mount, 3/4, Vigilance, Saddle 2.
 *
 *   "Whenever this creature attacks while saddled, create a 1/1 white Sheep creature token."
 *
 * Verifies the Mount's saddle-gated attack trigger: saddling (CR 702.171) then attacking spawns a
 * Sheep token, while attacking unsaddled produces none.
 */
class BridledBighornScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSaddled(id: EntityId): Boolean =
        state.getEntity(id)?.has<SaddledComponent>() == true

    test("attacking while saddled creates a 1/1 white Sheep token") {
        val driver = createDriver()
        val player = driver.player1

        val bighorn = driver.putCreatureOnBattlefield(player, "Bridled Bighorn")
        val bear = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // power 2 → pays Saddle 2
        driver.removeSummoningSickness(bighorn)

        val creaturesBefore = driver.getCreatures(player).size

        driver.submitSuccess(SaddleMount(player, bighorn, listOf(bear)))
        driver.bothPass()
        driver.isSaddled(bighorn) shouldBe true

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(bighorn), driver.player2)
        driver.bothPass() // resolve the attack trigger

        // One new Sheep token (Grizzly Bears + Bighorn + Sheep = creaturesBefore + 1).
        driver.getCreatures(player).size shouldBe creaturesBefore + 1
    }

    test("attacking while NOT saddled creates no token") {
        val driver = createDriver()
        val player = driver.player1

        val bighorn = driver.putCreatureOnBattlefield(player, "Bridled Bighorn")
        driver.removeSummoningSickness(bighorn)

        val creaturesBefore = driver.getCreatures(player).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(bighorn), driver.player2)
        driver.bothPass()

        driver.getCreatures(player).size shouldBe creaturesBefore
    }
})
