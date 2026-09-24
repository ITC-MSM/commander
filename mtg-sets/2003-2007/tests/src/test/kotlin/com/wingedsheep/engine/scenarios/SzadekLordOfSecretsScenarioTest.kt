package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Szadek, Lord of Secrets (RAV #234) — "If Szadek would deal combat damage to a player, instead
 * put that many +1/+1 counters on Szadek and that player mills that many cards."
 *
 * One replacement, two results: the damage is never dealt (no life lost), Szadek gets the
 * counters, and the damaged player mills the same number. Combat damage to a creature is
 * unaffected.
 */
class SzadekLordOfSecretsScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingLife = 20)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("unblocked combat damage becomes +1/+1 counters and a mill of the same size") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val szadek = d.putCreatureOnBattlefield(p1, "Szadek, Lord of Secrets")
        d.removeSummoningSickness(szadek)
        val libraryBefore = d.state.getLibrary(p2).size
        val graveyardBefore = d.getGraveyard(p2).size

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(szadek), p2)
        d.bothPass()
        d.declareNoBlockers(p2)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.assertLifeTotal(p2, 20)
        d.plusOneCounters(szadek) shouldBe 5
        (libraryBefore - d.state.getLibrary(p2).size) shouldBe 5
        (d.getGraveyard(p2).size - graveyardBefore) shouldBe 5
        d.state.projectedState.getPower(szadek) shouldBe 10
    }

    test("the counters it gained make the next hit bigger") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val szadek = d.putCreatureOnBattlefield(p1, "Szadek, Lord of Secrets")
        d.removeSummoningSickness(szadek)
        d.addComponent(
            szadek,
            CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 2)
        )
        val libraryBefore = d.state.getLibrary(p2).size

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(szadek), p2)
        d.bothPass()
        d.declareNoBlockers(p2)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // A 7/7 Szadek replaces 7 damage: 7 more counters (9 total) and a 7-card mill.
        d.assertLifeTotal(p2, 20)
        d.plusOneCounters(szadek) shouldBe 9
        (libraryBefore - d.state.getLibrary(p2).size) shouldBe 7
    }

    test("combat damage to a blocking creature is dealt normally") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val szadek = d.putCreatureOnBattlefield(p1, "Szadek, Lord of Secrets")
        d.removeSummoningSickness(szadek)
        // Serra Angel can block the flyer and dies to its 5 damage.
        val angel = d.putCreatureOnBattlefield(p2, "Serra Angel")
        val libraryBefore = d.state.getLibrary(p2).size

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(szadek), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(angel to listOf(szadek)))
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.getGraveyardCardNames(p2).contains("Serra Angel") shouldBe true
        d.plusOneCounters(szadek) shouldBe 0
        d.state.getLibrary(p2).size shouldBe libraryBefore
        d.assertLifeTotal(p2, 20)
    }
})
