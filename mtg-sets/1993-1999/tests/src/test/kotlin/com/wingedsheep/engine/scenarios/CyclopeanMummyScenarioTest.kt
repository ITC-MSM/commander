package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

/**
 * Tests for Cyclopean Mummy (Legends, {1}{B}, 2/1 Zombie).
 *
 * Oracle: "When this creature dies, exile it."
 *
 * A leaves-the-battlefield trigger "looks back in time" (CR 603.10a) and is indexed off the
 * battlefield, so the ability keeps `TriggeredAbility`'s default `activeZones = {BATTLEFIELD}`.
 * Narrowing it to the graveyard with `triggerZone` — which reads plausibly, since the effect
 * reaches for a card that is *already* in the graveyard when it resolves — replaces that default
 * rather than adding to it, and `TriggerDetector` then never indexes the trigger at all. This
 * test is the proof that the mummy's own death actually exiles it.
 */
class CyclopeanMummyScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("Cyclopean Mummy exiles itself when it dies in combat") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val defender = driver.activePlayer!!
        val attacker = driver.getOpponent(defender)

        val mummy = driver.putCreatureOnBattlefield(defender, "Cyclopean Mummy")
        driver.removeSummoningSickness(mummy)

        // A 3/3 kills the 2/1 mummy outright in the combat damage step.
        val giant = driver.putCreatureOnBattlefield(attacker, "Hill Giant")
        driver.removeSummoningSickness(giant)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (driver.activePlayer != attacker) {
            driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        }

        driver.declareAttackers(attacker, listOf(giant), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(mummy to listOf(giant)))

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // The dies trigger resolved: the mummy is in exile, never left sitting in the graveyard.
        driver.getExileCardNames(defender) shouldContain "Cyclopean Mummy"
        driver.getGraveyardCardNames(defender) shouldNotContain "Cyclopean Mummy"
    }
})
