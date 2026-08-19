package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseFileAuditor
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheGatewayExpress
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Case File Auditor — "When this creature enters **and whenever you solve a Case**, look at the top
 * six cards of your library. You may reveal an enchantment card from among them and put it into
 * your hand. Put the rest on the bottom of your library in a random order."
 *
 * The new vocabulary is `Triggers.WheneverYouSolveACase`, so the load-bearing assertions are:
 *
 *  1. solving a Case fires it — and fires it for the *solving* player, which is what the event's
 *     carried controller is for;
 *  2. it does **not** fire on the Case merely entering, nor on an opponent's Case being solved;
 *  3. the two printed conditions are two abilities, so an Auditor that entered earlier still sees a
 *     later solve.
 *
 * The look itself is the "up to one" shape: the pick is filtered to enchantments and declinable, so
 * a library of six non-enchantments must resolve with no prompt-forced pick and an unchanged hand.
 */
class CaseFileAuditorScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseFileAuditor)
        driver.registerCard(CaseOfTheGatewayExpress)
        driver.initMirrorMatch(Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Stack [names] so that `names.first()` ends up on top of [player]'s library. */
    fun GameTestDriver.stackTop(player: EntityId, names: List<String>) {
        names.reversed().forEach { putCardOnTopOfLibrary(player, it) }
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    test("the enters trigger looks at six and takes the enchantment") {
        val driver = newDriver()
        driver.stackTop(
            driver.player1,
            listOf("Plains", "Plains", "Case of the Gateway Express", "Plains", "Plains", "Plains")
        )
        val handBefore = driver.getHand(driver.player1).size

        val card = driver.putCardInHand(driver.player1, "Case File Auditor")
        driver.giveMana(driver.player1, Color.WHITE, 3)
        driver.castSpell(driver.player1, card).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; the enters trigger goes on the stack
        driver.bothPass() // resolve the trigger

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("declinable — 'you may reveal'") { decision.minSelections shouldBe 0 }
        withClue("only the enchantment among the six is selectable") {
            decision.options.size shouldBe 1
            decision.maxSelections shouldBe 1
        }
        withClue("but all six are shown — the card says 'look at the top six'") {
            decision.options.size + decision.nonSelectableOptions.size shouldBe 6
        }
        val enchantment = decision.options.single()
        driver.submitCardSelection(driver.player1, listOf(enchantment))

        withClue("the Case goes to hand; the Auditor itself left the hand when cast") {
            driver.getHand(driver.player1).contains(enchantment) shouldBe true
            driver.getHand(driver.player1).size shouldBe handBefore + 1
        }
        withClue("the other five are bottomed, not exiled or milled") {
            driver.state.getZone(driver.player1, Zone.LIBRARY).size shouldBe
                driver.state.getZone(driver.player1, Zone.LIBRARY).distinct().size
            driver.state.getZone(driver.player1, Zone.GRAVEYARD).isEmpty() shouldBe true
        }
    }

    test("declining takes nothing and bottoms all six") {
        val driver = newDriver()
        driver.stackTop(
            driver.player1,
            listOf("Plains", "Plains", "Case of the Gateway Express", "Plains", "Plains", "Plains")
        )
        val librarySize = driver.state.getZone(driver.player1, Zone.LIBRARY).size
        val handBefore = driver.getHand(driver.player1).size

        val card = driver.putCardInHand(driver.player1, "Case File Auditor")
        driver.giveMana(driver.player1, Color.WHITE, 3)
        driver.castSpell(driver.player1, card).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        driver.submitCardSelection(driver.player1, emptyList())

        driver.getHand(driver.player1).size shouldBe handBefore
        withClue("nothing left the library") {
            driver.state.getZone(driver.player1, Zone.LIBRARY).size shouldBe librarySize
        }
    }

    test("solving a Case fires the second ability on an Auditor already in play") {
        val driver = newDriver()
        val auditor = driver.putCreatureOnBattlefield(driver.player1, "Case File Auditor")
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Gateway Express")
        val a = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val b = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val c = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        listOf(a, b, c).forEach { driver.removeSummoningSickness(it) }
        driver.stackTop(
            driver.player1,
            listOf("Plains", "Plains", "Case of the Gateway Express", "Plains", "Plains", "Plains")
        )

        withClue("putting the Case into play does not solve it, so nothing has fired yet") {
            driver.isSolved(case) shouldBe false
            (driver.pendingDecision is SelectCardsDecision) shouldBe false
        }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(a, b, c), driver.player2)
        driver.passPriorityUntil(Step.END)
        driver.bothPass() // the "To solve" trigger resolves and stamps the designation
        driver.isSolved(case) shouldBe true

        driver.bothPass() // the Auditor's solve trigger resolves

        val decision = driver.pendingDecision
        withClue("the Auditor was already on the battlefield and still sees the solve") {
            decision.shouldBeInstanceOf<SelectCardsDecision>()
            decision.playerId shouldBe driver.player1
        }
        driver.state.getBattlefield().contains(auditor) shouldBe true
    }

    test("an opponent solving their own Case does not fire your Auditor") {
        val driver = newDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Case File Auditor")
        val theirCase = driver.putPermanentOnBattlefield(driver.player2, "Case of the Gateway Express")
        val a = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val b = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val c = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        listOf(a, b, c).forEach { driver.removeSummoningSickness(it) }

        // Hand the turn to player 2 so their creatures can attack and their Case can solve.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player2, listOf(a, b, c), driver.player1)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.isSolved(theirCase) shouldBe true
        withClue("\"whenever YOU solve a Case\" — the solving player is the Case's controller") {
            (driver.pendingDecision is SelectCardsDecision) shouldBe false
        }
    }
})
