package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ons.cards.MiseryCharm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Misery Charm (ONS #158).
 *
 * Misery Charm: {B}
 * Instant
 * Choose one —
 * • Destroy target Cleric.
 * • Return target Cleric card from your graveyard to your hand.
 * • Target player loses 2 life.
 *
 * Mode selection happens at CAST time (CR 601.2b / 700.2a). Modes whose targets can't be
 * satisfied are filtered out of the mode picker, so a Cleric-destroy mode is not offered
 * when no Cleric is on the battlefield.
 */
class MiseryCharmTest : FunSpec({

    val TestCleric = CardDefinition.creature(
        name = "Test Cleric",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Human"), Subtype("Cleric")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TestCleric))
        return driver
    }

    test("mode 1 - destroy target Cleric") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opponent, "Test Cleric")

        driver.giveMana(activePlayer, Color.BLACK, 1)
        val charm = driver.putCardInHand(activePlayer, "Misery Charm")
        driver.castSpell(activePlayer, charm)

        // Mode selection happens at cast time, before the spell is on the stack.
        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(activePlayer, OptionChosenResponse(modeDecision.id, 0))

        // Per-mode targets (also cast-time).
        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        val clericId = targetDecision.legalTargets.values.first().first()
        driver.submitTargetSelection(activePlayer, listOf(clericId))

        // Drain priority to resolve the spell from the stack.
        driver.bothPass()

        driver.assertInGraveyard(opponent, "Test Cleric")
    }

    test("mode 1 (destroy Cleric) is not offered when no Cleric is on the battlefield") {
        // CR 700.2a — a mode whose targets can't be legally chosen isn't an option.
        // The cast-time mode picker therefore filters out the Cleric-destroy mode when
        // no Cleric exists. (Pre-refactor this test verified the spell-fizzles-on-resolve
        // shape; that path is no longer reachable because the bad mode pick is prevented.)
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.giveMana(activePlayer, Color.BLACK, 1)
        val charm = driver.putCardInHand(activePlayer, "Misery Charm")
        driver.castSpell(activePlayer, charm)

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        val offeredModes = modeDecision.options
        // Mode 0 ("Destroy target Cleric") and mode 1 ("Return target Cleric ... from
        // your graveyard") both need a Cleric somewhere; neither should be offered.
        offeredModes.none { it.contains("Cleric", ignoreCase = true) } shouldBe true
        // Mode 2 ("Target player loses 2 life") is always satisfiable.
        offeredModes.any { it.contains("loses 2 life", ignoreCase = true) } shouldBe true

        driver.assertPermanentExists(opponent, "Grizzly Bears")
    }

    test("mode 2 - return target Cleric card from graveyard to hand") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(activePlayer, "Test Cleric")

        driver.giveMana(activePlayer, Color.BLACK, 1)
        val charm = driver.putCardInHand(activePlayer, "Misery Charm")
        driver.castSpell(activePlayer, charm)

        // Cast-time mode pause
        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        val returnModeIndex = modeDecision.options.indexOfFirst { it.startsWith("Return target Cleric") }
        check(returnModeIndex >= 0) { "Couldn't find return-Cleric mode in ${modeDecision.options}" }
        driver.submitDecision(activePlayer, OptionChosenResponse(modeDecision.id, returnModeIndex))

        // Cast-time per-mode target pause
        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        val clericId = targetDecision.legalTargets.values.first().first()
        driver.submitTargetSelection(activePlayer, listOf(clericId))

        driver.bothPass()

        val hand = driver.state.getHand(activePlayer)
        val clericInHand = hand.any { entityId ->
            driver.state.getEntity(entityId)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.name == "Test Cleric"
        }
        clericInHand shouldBe true
    }

    test("mode 3 - target player loses 2 life") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(activePlayer, Color.BLACK, 1)
        val charm = driver.putCardInHand(activePlayer, "Misery Charm")
        driver.castSpell(activePlayer, charm)

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        val loseLifeIndex = modeDecision.options.indexOfFirst { it.contains("loses 2 life") }
        check(loseLifeIndex >= 0) { "Couldn't find lose-life mode in ${modeDecision.options}" }
        driver.submitDecision(activePlayer, OptionChosenResponse(modeDecision.id, loseLifeIndex))

        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(activePlayer, listOf(opponent))

        driver.bothPass()

        driver.assertLifeTotal(opponent, 18)
    }

    test("mode 3 - can target yourself") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(activePlayer, Color.BLACK, 1)
        val charm = driver.putCardInHand(activePlayer, "Misery Charm")
        driver.castSpell(activePlayer, charm)

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        val loseLifeIndex = modeDecision.options.indexOfFirst { it.contains("loses 2 life") }
        check(loseLifeIndex >= 0) { "Couldn't find lose-life mode in ${modeDecision.options}" }
        driver.submitDecision(activePlayer, OptionChosenResponse(modeDecision.id, loseLifeIndex))

        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(activePlayer, listOf(activePlayer))

        driver.bothPass()

        driver.assertLifeTotal(activePlayer, 18)
    }
})
