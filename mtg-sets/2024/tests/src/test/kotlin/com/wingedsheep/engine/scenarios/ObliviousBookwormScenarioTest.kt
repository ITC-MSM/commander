package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.MorphDataComponent
import com.wingedsheep.engine.state.components.player.PermanentEnteredFaceDownThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.ObliviousBookworm
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Oblivious Bookworm (DSK).
 *
 * "At the beginning of your end step, you may draw a card. If you do, discard a card unless a
 * permanent entered the battlefield face down under your control this turn or you turned a
 * permanent face up this turn."
 *
 * Verifies the optional draw, the conditional ("unless") discard, and that either face-down
 * trigger satisfies the unless-clause so no discard is required.
 */
class ObliviousBookwormScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ObliviousBookworm))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Island" to 20), skipMulligans = true)
        return driver
    }

    // Put a face-down 2/2 morph creature directly on the battlefield (mirrors MorphCostPaymentTest).
    fun GameTestDriver.putFaceDownCreature(playerId: EntityId, cardName: String): EntityId {
        val id = putCreatureOnBattlefield(playerId, cardName)
        val cardDef = cardRegistry.requireCard(cardName)
        val morph = cardDef.keywordAbilities.filterIsInstance<KeywordAbility.Morph>().firstOrNull()
        replaceState(state.updateEntity(id) { c ->
            var u = c.with(FaceDownComponent)
            if (morph != null) u = u.with(MorphDataComponent(morph.morphCost, cardDef.name))
            u
        })
        return id
    }

    /** Advance to the active player's end step and resolve the Bookworm trigger to its decision. */
    fun GameTestDriver.toEndStepMayDraw(): YesNoDecision {
        passPriorityUntil(Step.END)
        // The end-step trigger is on the stack; resolve it to surface the "may draw" decision.
        var guard = 0
        while (pendingDecision !is YesNoDecision && guard++ < 8) bothPass()
        return pendingDecision as YesNoDecision
    }

    test("draw, then discard when no face-down condition is met") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Oblivious Bookworm")

        val handBefore = driver.getHandSize(player)
        val may = driver.toEndStepMayDraw()
        driver.submitYesNo(may.playerId, true) // draw a card

        // No face-down event this turn → must discard. A discard selection decision appears.
        driver.pendingDecision.shouldNotBeNull()
        driver.autoResolveDecision() // discard the chosen card

        // Net hand size unchanged: +1 draw, -1 discard.
        driver.getHandSize(player) shouldBe handBefore
    }

    test("no discard when a permanent entered the battlefield face down this turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Oblivious Bookworm")

        // Mark that a permanent entered face down under this player's control this turn.
        driver.replaceState(driver.state.updateEntity(player) { c ->
            c.with(PermanentEnteredFaceDownThisTurnComponent(1))
        })

        val handBefore = driver.getHandSize(player)
        val may = driver.toEndStepMayDraw()
        driver.submitYesNo(may.playerId, true) // draw a card

        // The unless-clause holds → no discard decision, hand grows by the drawn card.
        (driver.pendingDecision == null) shouldBe true
        driver.getHandSize(player) shouldBe handBefore + 1
    }

    test("no discard when you turned a permanent face up this turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Oblivious Bookworm")

        // Turn a face-down creature face up this turn via the real special action — this exercises
        // the TurnFaceUpHandler tracker hook.
        val morphling = driver.putFaceDownCreature(player, "Morph Test Creature")
        driver.giveMana(player, com.wingedsheep.sdk.core.Color.WHITE, 2)
        driver.submit(TurnFaceUp(playerId = player, sourceId = morphling)).error shouldBe null
        driver.state.getEntity(morphling)?.get<FaceDownComponent>() shouldBe null

        val handBefore = driver.getHandSize(player)
        val may = driver.toEndStepMayDraw()
        driver.submitYesNo(may.playerId, true) // draw a card

        (driver.pendingDecision == null) shouldBe true
        driver.getHandSize(player) shouldBe handBefore + 1
    }

    test("declining the optional draw does nothing") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Oblivious Bookworm")

        val handBefore = driver.getHandSize(player)
        val may = driver.toEndStepMayDraw()
        driver.submitYesNo(may.playerId, false) // decline

        (driver.pendingDecision == null) shouldBe true
        driver.getHandSize(player) shouldBe handBefore
    }
})
