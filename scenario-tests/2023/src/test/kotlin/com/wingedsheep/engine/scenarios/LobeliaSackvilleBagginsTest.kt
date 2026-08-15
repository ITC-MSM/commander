package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.LobeliaSackvilleBaggins
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Lobelia Sackville-Baggins (LTR) — ETB target "creature card from an opponent's graveyard
 * put there from the battlefield this turn" (Gap 20). Exiles the chosen card, then creates X
 * Treasure tokens where X = its power (exercises `CreatePredefinedTokenEffect.dynamicCount`).
 */
class LobeliaSackvilleBagginsTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all +
                com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                listOf(LobeliaSackvilleBaggins)
        )
        return driver
    }

    fun GameTestDriver.treasuresControlledBy(playerId: EntityId): Int =
        state.getBattlefield().count { id ->
            state.getEntity(id)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.name == "Treasure" &&
                state.projectedState.getController(id) == playerId
        }

    test("exiles opponent's creature card and makes Treasures equal to its power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opp = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent's 2/2 in their graveyard, Gap 20 marker set by ZoneTransitionService.
        val oppBear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val mv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = oppBear,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(mv.state)

        val treasuresBefore = driver.treasuresControlledBy(active)
        val lobelia = driver.putCardInHand(active, "Lobelia Sackville-Baggins")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveColorlessMana(active, 2)
        driver.castSpell(active, lobelia).isSuccess shouldBe true
        driver.bothPass() // resolve Lobelia → ETB pauses for target selection

        driver.submitTargetSelection(active, listOf(oppBear))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Grizzly Bears was 2/2 → 2 Treasures.
        (driver.treasuresControlledBy(active) - treasuresBefore) shouldBe 2
    }
})
