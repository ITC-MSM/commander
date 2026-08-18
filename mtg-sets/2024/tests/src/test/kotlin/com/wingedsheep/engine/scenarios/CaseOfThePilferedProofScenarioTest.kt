package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfThePilferedProof
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Pilfered Proof — {1}{W} Enchantment — Case.
 *
 * Two halves worth pinning: the +1/+1 counter goes on the *Detective that entered*, not on the
 * Case and not on non-Detectives; and the Solved line is a replacement effect that only applies
 * while the Case is solved — the gate rides in the replacement's own restrictions, which is the
 * path this card's engine change opened up.
 */
class CaseOfThePilferedProofScenarioTest : FunSpec({

    val testDetective = card("Test Sleuth") {
        manaCost = "{W}"
        typeLine = "Creature — Human Detective"
        power = 1
        toughness = 1
        oracleText = ""
    }

    val testCivilian = card("Test Civilian") {
        manaCost = "{W}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
        oracleText = ""
    }

    /** A {1} sorcery that makes one plain token, to trigger the Clue rider. */
    val testTokenMaker = card("Test Deputize") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Create a 1/1 white Soldier creature token."
        spell {
            effect = Effects.CreateToken(
                name = "Soldier",
                power = 1,
                toughness = 1,
                creatureTypes = setOf("Soldier"),
                colors = setOf(Color.WHITE)
            )
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(CaseOfThePilferedProof)
        driver.registerCard(testDetective)
        driver.registerCard(testCivilian)
        driver.registerCard(testTokenMaker)
        driver.initMirrorMatch(Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    fun GameTestDriver.castCreature(name: String): EntityId {
        val card = putCardInHand(player1, name)
        giveMana(player1, Color.WHITE, 1)
        castSpell(player1, card).isSuccess shouldBe true
        bothPass() // resolve the creature; any counter trigger goes on the stack
        bothPass() // resolve the counter trigger
        return card
    }

    fun GameTestDriver.clueCount(): Int =
        getPermanents(player1).count { getCardName(it) == "Clue" }

    test("a Detective you control entering gets a +1/+1 counter — on it, not on the Case") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Pilfered Proof")

        val sleuth = driver.castCreature("Test Sleuth")

        driver.state.projectedState.getPower(sleuth) shouldBe 2
        driver.state.projectedState.getToughness(sleuth) shouldBe 2
        driver.state.getEntity(case)?.get<com.wingedsheep.engine.state.components.battlefield.CountersComponent>() shouldBe null
    }

    test("a non-Detective creature entering gets nothing") {
        val driver = newDriver()
        driver.putPermanentOnBattlefield(driver.player1, "Case of the Pilfered Proof")

        val civilian = driver.castCreature("Test Civilian")

        driver.state.projectedState.getPower(civilian) shouldBe 1
    }

    test("three Detectives solve it (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Pilfered Proof")
        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")
        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — a token creation also makes a Clue; unsolved it does not (CR 702.169b)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Pilfered Proof")

        // Unsolved: one Soldier, no Clue.
        val spell = driver.putCardInHand(driver.player1, "Test Deputize")
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, spell).isSuccess shouldBe true
        driver.bothPass()
        driver.clueCount() shouldBe 0

        // Solve it.
        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")
        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")
        driver.putCreatureOnBattlefield(driver.player1, "Test Sleuth")
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Solved: the same spell now also yields a Clue.
        val spell2 = driver.putCardInHand(driver.player1, "Test Deputize")
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, spell2).isSuccess shouldBe true
        driver.bothPass()
        driver.clueCount() shouldBe 1
    }
})
