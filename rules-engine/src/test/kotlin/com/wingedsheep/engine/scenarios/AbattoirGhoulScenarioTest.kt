package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.AbattoirGhoul
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Abattoir Ghoul: first strike + "whenever a creature dealt damage by this this turn dies,
 * gain life equal to that creature's toughness."
 */
class AbattoirGhoulScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AbattoirGhoul)
        return driver
    }

    test("gains life equal to toughness when a creature it damaged dies in combat") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val ghoul = driver.putCreatureOnBattlefield(attacker, "Abattoir Ghoul")
        driver.removeSummoningSickness(ghoul)

        // 2/2 blocker dies to first-strike 3 damage; toughness LKI is 2.
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(ghoul), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(blocker to listOf(ghoul)))

        // First-strike damage kills the 2/2; dies trigger gains 2 life.
        driver.passPriorityUntil(Step.END_COMBAT)
        driver.bothPass()

        driver.getLifeTotal(attacker) shouldBe 22
        driver.findPermanent(defender, "Grizzly Bears") shouldBe null
    }
})
