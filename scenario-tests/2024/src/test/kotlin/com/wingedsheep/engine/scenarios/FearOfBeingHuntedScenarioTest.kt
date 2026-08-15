package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfBeingHunted
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Being Hunted (DSK #134) — "Haste. This creature must be blocked if able."
 *
 * Exercises the unconditional `MustBeBlocked` static ability (allCreatures = false) honored by the
 * block-phase validation: a defender that can block must do so.
 */
class FearOfBeingHuntedScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(FearOfBeingHunted))
        return d
    }

    test("must be blocked if able — declining to block is illegal") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        driver.removeSummoningSickness(nightmare)
        driver.putCreatureOnBattlefield(p2, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // p2 controls a Grizzly Bears that can block, so declaring no blockers is illegal.
        driver.declareBlockers(p2, emptyMap()).isSuccess shouldBe false

        // Blocking the attacker is legal.
        val bears = driver.findPermanent(p2, "Grizzly Bears")!!
        driver.declareBlockers(p2, mapOf(bears to listOf(nightmare))).isSuccess shouldBe true
    }

    test("no requirement when the defender has no creature able to block") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        driver.removeSummoningSickness(nightmare)
        // p2 has no creatures — nothing is able to block, so declaring no blockers is legal.

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        driver.declareBlockers(p2, emptyMap()).isSuccess shouldBe true
    }
})
