package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.ScriedEvent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RuinLurkerBat
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ruin-Lurker Bat (LCI #33): {W} 1/1 Bat — Flying, lifelink. "At the beginning of
 * your end step, if you descended this turn, scry 1."
 *
 * Engine-level coverage of the descend tracker itself lives in
 * [DescendTrackerTest]. This test just confirms the card wires the EOT trigger,
 * the descend intervening-if, and the scry payload together correctly — both
 * the gated-off and gated-on paths — by checking whether a `ScriedEvent` ever
 * lands.
 */
class RuinLurkerBatScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RuinLurkerBat))
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    // Drain through scry's two prompts (bottom-pick, top-reorder) without changing
    // the order — same pattern used by ScryTriggerScenarioTest.
    fun GameTestDriver.drainScryDecisions(player: EntityId) {
        repeat(4) {
            when (val decision = pendingDecision) {
                is SelectCardsDecision ->
                    submitDecision(player, CardsSelectedResponse(decision.id, emptyList()))
                is ReorderLibraryDecision ->
                    submitDecision(player, OrderedResponse(decision.id, decision.cards))
                else -> return
            }
        }
    }

    test("no scry trigger fires at end step when the controller has not descended") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Ruin-Lurker Bat")

        val before = driver.events.size
        driver.passPriorityUntil(Step.END)
        driver.bothPass() // pass the end step
        driver.drainScryDecisions(player)

        driver.events.drop(before).filterIsInstance<ScriedEvent>() shouldBe emptyList()
    }

    test("scry 1 fires at end step when the controller has descended this turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Ruin-Lurker Bat")

        // Send a permanent (creature) card from hand to graveyard — this is the
        // canonical "you descended this turn" event for a card with no real
        // discard outlet on the board.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        val transition = ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = bears,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(transition.state)

        val before = driver.events.size
        driver.passPriorityUntil(Step.END)
        driver.bothPass() // let the EOT trigger go on the stack and start resolving
        driver.drainScryDecisions(player)

        val scried = driver.events.drop(before).filterIsInstance<ScriedEvent>()
        scried.size shouldBe 1
        scried.single().playerId shouldBe player
        scried.single().count shouldBe 1
    }
})
