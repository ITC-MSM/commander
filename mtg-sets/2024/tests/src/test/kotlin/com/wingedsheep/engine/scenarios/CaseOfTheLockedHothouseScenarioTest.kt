package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheLockedHothouse
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Locked Hothouse — {3}{G} Enchantment — Case.
 *
 * The unsolved half is the extra land drop; the Solved half is a top-of-library permission that is
 * *gated*, which is the interesting bit — the top card must stay unplayable until the Case is
 * solved, and then only lands, creatures, and enchantments become playable from there.
 */
class CaseOfTheLockedHothouseScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheLockedHothouse)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    fun GameTestDriver.libraryActions(): List<String> =
        legalActions(player1).filter { it.sourceZone == "LIBRARY" }.map { it.description }

    /** Solve the Case by walking to the end step with seven lands out. */
    fun GameTestDriver.solve(case: EntityId) {
        repeat(7 - getLands(player1).size) { putLandOnBattlefield(player1, "Forest") }
        passPriorityUntil(Step.END)
        bothPass()
        isSolved(case) shouldBe true
    }

    test("you may play an additional land each turn even while unsolved") {
        val driver = newDriver()
        driver.putPermanentOnBattlefield(driver.player1, "Case of the Locked Hothouse")

        val first = driver.putCardInHand(driver.player1, "Forest")
        val second = driver.putCardInHand(driver.player1, "Forest")
        driver.playLand(driver.player1, first).isSuccess shouldBe true
        driver.playLand(driver.player1, second).isSuccess shouldBe true

        driver.getLands(driver.player1).size shouldBe 2
    }

    test("seven lands solve it (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Locked Hothouse")

        repeat(6) { driver.putLandOnBattlefield(driver.player1, "Forest") }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.solve(case)
    }

    test("the top card is only playable once solved, and only if it's a land, creature or enchantment") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Locked Hothouse")
        driver.putCardOnTopOfLibrary(driver.player1, "Grizzly Bears")

        driver.libraryActions().isEmpty() shouldBe true

        driver.solve(case)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardOnTopOfLibrary(driver.player1, "Grizzly Bears")
        driver.libraryActions().any { it.contains("Grizzly Bears") } shouldBe true

        // A land on top is playable too — the permission always covers lands.
        driver.putCardOnTopOfLibrary(driver.player1, "Forest")
        driver.libraryActions().any { it.contains("Forest") } shouldBe true
    }
})
