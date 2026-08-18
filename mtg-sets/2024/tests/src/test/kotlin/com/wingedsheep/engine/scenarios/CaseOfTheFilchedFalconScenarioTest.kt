package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheFilchedFalcon
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Filched Falcon — {U} Enchantment — Case.
 *
 * The Solved ability is the part that can go quietly wrong: a 0/0 with four +1/+1 counters is a
 * 4/4 only if the counters and the animate land in the same resolution, and "in addition to its
 * other types" means the artifact keeps being an artifact.
 */
class CaseOfTheFilchedFalconScenarioTest : FunSpec({

    /** A plain {1} artifact to animate. */
    val testRelic = card("Test Relic") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = ""
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(CaseOfTheFilchedFalcon)
        driver.registerCard(testRelic)
        driver.initMirrorMatch(Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    test("the enters trigger investigates") {
        val driver = newDriver()
        val card = driver.putCardInHand(driver.player1, "Case of the Filched Falcon")
        driver.giveMana(driver.player1, Color.BLUE, 1)
        driver.castSpell(driver.player1, card).isSuccess shouldBe true
        driver.bothPass() // Case resolves, enters trigger on the stack
        driver.bothPass() // trigger resolves

        driver.getPermanents(driver.player1).count { driver.getCardName(it) == "Clue" } shouldBe 1
    }

    test("three artifacts solve it — the Clue it made is one of them") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Filched Falcon")
        driver.putPermanentOnBattlefield(driver.player1, "Test Relic")
        driver.putPermanentOnBattlefield(driver.player1, "Test Relic")

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        driver.putPermanentOnBattlefield(driver.player1, "Test Relic")
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — the artifact becomes a 4/4 flying Bird that is still an artifact") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Filched Falcon")
        val relic = driver.putPermanentOnBattlefield(driver.player1, "Test Relic")
        driver.putPermanentOnBattlefield(driver.player1, "Test Relic")
        driver.putPermanentOnBattlefield(driver.player1, "Test Relic")

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(driver.player1, Color.BLUE, 3)
        val ability = CaseOfTheFilchedFalcon.activatedAbilities.first().id
        driver.submitSuccess(
            ActivateAbility(
                driver.player1, case, ability,
                targets = listOf(ChosenTarget.Permanent(relic))
            )
        )
        driver.bothPass()

        val projected = driver.state.projectedState
        // Base 0/0 plus four +1/+1 counters — and it survived the state-based actions.
        driver.state.getBattlefield().contains(relic) shouldBe true
        projected.getPower(relic) shouldBe 4
        projected.getToughness(relic) shouldBe 4
        projected.isCreature(relic) shouldBe true
        projected.hasSubtype(relic, "Bird") shouldBe true
        projected.hasKeyword(relic, Keyword.FLYING) shouldBe true
        projected.hasType(relic, "ARTIFACT") shouldBe true
        // The Case sacrificed itself paying the cost.
        driver.state.getBattlefield().contains(case) shouldBe false
    }
})
