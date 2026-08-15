package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.TheRingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.SamwiseTheStouthearted
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Samwise the Stouthearted (LTR) — ETB target "permanent card in your graveyard that was put
 * there from the battlefield this turn" (Gap 20). Returns the chosen card to hand, then the
 * Ring tempts you.
 *
 * The "Then the Ring tempts you" rider must fire on every resolution — including when no
 * target was chosen (because the player declined the "up to one" choice or no valid target
 * existed). "Then" sequences effects within the same ability; it isn't conditional on the
 * return succeeding.
 */
class SamwiseTheStoutheartedTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SamwiseTheStouthearted))
        return driver
    }

    fun GameTestDriver.ringTempts(playerId: EntityId): Int =
        state.getEntity(playerId)?.get<TheRingComponent>()?.temptCount ?: 0

    test("returns a permanent card put into your graveyard from the battlefield this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stage a permanent in your graveyard that arrived via battlefield this turn —
        // moving via ZoneTransitionService sets the Gap 20 marker.
        val bear = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val mv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = bear,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(mv.state)

        val samwise = driver.putCardInHand(active, "Samwise the Stouthearted")
        driver.giveMana(active, Color.WHITE, 1)
        driver.giveColorlessMana(active, 1)
        val handBefore = driver.getHandSize(active)
        val temptsBefore = driver.ringTempts(active)
        driver.castSpell(active, samwise).isSuccess shouldBe true
        driver.bothPass()

        driver.submitTargetSelection(active, listOf(bear))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getHandSize(active) shouldBe handBefore
        driver.state.getGraveyard(active).contains(bear) shouldBe false
        // Ring tempted you exactly once.
        driver.ringTempts(active) shouldBe temptsBefore + 1
    }

    test("Ring tempts you ALSO fires when the player declines the optional return target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stage a valid graveyard card so the ETB trigger goes on the stack with a real
        // "up to one" target prompt — the player can either pick the card or decline.
        val bear = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val mv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = bear,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(mv.state)

        val samwise = driver.putCardInHand(active, "Samwise the Stouthearted")
        driver.giveMana(active, Color.WHITE, 1)
        driver.giveColorlessMana(active, 1)
        val temptsBefore = driver.ringTempts(active)
        driver.castSpell(active, samwise).isSuccess shouldBe true
        driver.bothPass()

        // Decline the optional target — submit zero selections.
        driver.submitTargetSelection(active, emptyList())
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // The bear stays in the graveyard (nothing returned) BUT the Ring still tempted you.
        driver.state.getGraveyard(active).contains(bear) shouldBe true
        driver.ringTempts(active) shouldBe temptsBefore + 1
    }
})
