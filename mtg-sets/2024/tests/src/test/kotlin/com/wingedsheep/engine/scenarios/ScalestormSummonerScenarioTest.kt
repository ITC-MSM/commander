package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scalestorm Summoner (OTJ #144) — {2}{R} Human Warlock, 3/3.
 *
 *   "Whenever this creature attacks, create a 3/1 red Dinosaur creature token if you control a
 *    creature with power 4 or greater."
 *
 * Verifies the intervening-"if" attack trigger: a Dinosaur token appears only when a power-4+
 * creature is also under your control as the trigger resolves.
 */
class ScalestormSummonerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.dinosaurs(playerId: EntityId) =
        getCreatures(playerId).count {
            state.getEntity(it)?.get<CardComponent>()?.name == "Dinosaur Token"
        }

    test("attacking with a power-4+ creature in play creates a 3/1 Dinosaur token") {
        val driver = createDriver()
        val player = driver.player1

        val summoner = driver.putCreatureOnBattlefield(player, "Scalestorm Summoner")
        // Hill Giant is a 3/3 — too small; use a 4/4+ creature for the condition.
        driver.putCreatureOnBattlefield(player, "Lumengrid Gargoyle") // 4/4 artifact creature
        driver.removeSummoningSickness(summoner)

        driver.dinosaurs(player) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(summoner), driver.player2)
        driver.bothPass() // resolve the attack trigger

        driver.dinosaurs(player) shouldBe 1
    }

    test("attacking without a power-4+ creature creates no token") {
        val driver = createDriver()
        val player = driver.player1

        val summoner = driver.putCreatureOnBattlefield(player, "Scalestorm Summoner")
        driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2 — below threshold
        driver.removeSummoningSickness(summoner)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(summoner), driver.player2)
        driver.bothPass()

        driver.dinosaurs(player) shouldBe 0
    }
})
