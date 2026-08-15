package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Akawalli, the Seething Tower (LCI #220) — descend-8 "can't be blocked by more than one creature."
 *
 * Regression test for the conditional block-restriction fix. `BlockPhaseManager.
 * validateMaxBlockersRequirements` previously collected `CantBeBlockedByMoreThan` via
 * `filterIsInstance`, which skips the `ConditionalStaticAbility`-wrapped form — so Akawalli's
 * descend-8 clause (and Cavern Stomper's identical wrapping) silently no-op'd. The evaluator now
 * unwraps the conditional and honors the max-blocker cap only while the descend-8 condition holds.
 *
 * Cases:
 *  - descend 8 active (≥8 permanent cards in graveyard): two blockers is illegal, one is legal.
 *  - descend 8 inactive (only 4 permanent cards): the condition is false, so two blockers is legal
 *    again — proving the cap is gated on the condition, not always-on.
 */
class AkawalliTheSeethingTowerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("descend 8 active — Akawalli can't be blocked by two creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val akawalli = driver.putCreatureOnBattlefield(attacker, "Akawalli, the Seething Tower")
        driver.removeSummoningSickness(akawalli)
        // Eight permanent cards in the attacker's graveyard → descend 8 (and 4) hold.
        repeat(8) { driver.putCardInGraveyard(attacker, "Grizzly Bears") }
        val blocker1 = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        val blocker2 = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(akawalli), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        // Two blockers on Akawalli violates its descend-8 "can't be blocked by more than one".
        driver.declareBlockers(
            defender,
            mapOf(blocker1 to listOf(akawalli), blocker2 to listOf(akawalli))
        ).isSuccess shouldBe false
    }

    test("descend 8 active — a single blocker is still legal") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val akawalli = driver.putCreatureOnBattlefield(attacker, "Akawalli, the Seething Tower")
        driver.removeSummoningSickness(akawalli)
        repeat(8) { driver.putCardInGraveyard(attacker, "Grizzly Bears") }
        val blocker1 = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(akawalli), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(blocker1 to listOf(akawalli))).isSuccess shouldBe true
    }

    test("descend 8 inactive — Akawalli may be blocked by two creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val akawalli = driver.putCreatureOnBattlefield(attacker, "Akawalli, the Seething Tower")
        driver.removeSummoningSickness(akawalli)
        // Only four permanent cards — descend 4 holds but descend 8 does not, so no max-blocker cap.
        repeat(4) { driver.putCardInGraveyard(attacker, "Grizzly Bears") }
        val blocker1 = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        val blocker2 = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(akawalli), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(
            defender,
            mapOf(blocker1 to listOf(akawalli), blocker2 to listOf(akawalli))
        ).isSuccess shouldBe true
    }
})
