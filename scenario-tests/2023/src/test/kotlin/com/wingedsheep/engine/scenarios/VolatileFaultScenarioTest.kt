package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.StripMine
import com.wingedsheep.mtg.sets.definitions.lci.cards.VolatileFault
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Volatile Fault (LCI #286).
 *
 * Land — Cave
 *  {T}: Add {C}.
 *  {1}, {T}, Sacrifice this land: Destroy target nonbasic land an opponent controls. That player may
 *    search their library for a basic land card, put it onto the battlefield, then shuffle. You
 *    create a Treasure token.
 *
 * The Demolition Field pattern with a cheaper ({1}) cost: the destroyed land's controller gets the
 * optional basic-land fetch, and instead of the activator fetching a land they create a Treasure
 * token. Exercises the whole chain — sacrifice cost, target nonbasic land dies, opponent's optional
 * fetch (both accept and decline), and the Treasure token creation.
 */
class VolatileFaultScenarioTest : FunSpec({

    // Index 0 is the "{T}: Add {C}" mana ability; index 1 is the sacrifice ability.
    val sacAbilityId = VolatileFault.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(VolatileFault, StripMine, PredefinedTokens.Treasure))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return driver
    }

    fun forestsControlledBy(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getBattlefield().count { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == "Forest" &&
                driver.state.projectedState.getController(id) == playerId
        }

    fun treasuresControlledBy(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getBattlefield().count { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == "Treasure" &&
                driver.state.projectedState.getController(id) == playerId
        }

    test("destroys the opponent's nonbasic land; opponent fetches a basic; you get a Treasure") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fault = driver.putLandOnBattlefield(player, "Volatile Fault")
        val stripMine = driver.putLandOnBattlefield(opponent, "Strip Mine")
        driver.giveColorlessMana(player, 1) // pays the {1}

        forestsControlledBy(driver, opponent) shouldBe 0
        treasuresControlledBy(driver, player) shouldBe 0

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = fault,
                abilityId = sacAbilityId,
                targets = listOf(ChosenTarget.Permanent(stripMine)),
            )
        ).isSuccess shouldBe true

        // Resolve the ability, accepting the opponent's optional basic-land search.
        var safety = 0
        while (safety < 40) {
            when (val pending = driver.state.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(pending.playerId, true)
                is SelectCardsDecision -> driver.submitCardSelection(pending.playerId, pending.options.take(1))
                else -> if (driver.stackSize > 0) driver.bothPass() else break
            }
            safety++
        }

        // Volatile Fault sacrificed itself as a cost.
        driver.findPermanent(player, "Volatile Fault") shouldBe null
        // The targeted Strip Mine was destroyed.
        driver.findPermanent(opponent, "Strip Mine") shouldBe null
        // The destroyed land's controller (the opponent) fetched a basic onto the battlefield.
        forestsControlledBy(driver, opponent) shouldBe 1
        // The activating player created a Treasure token.
        treasuresControlledBy(driver, player) shouldBe 1
    }

    test("opponent may decline the search; the land still dies and you still get a Treasure") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fault = driver.putLandOnBattlefield(player, "Volatile Fault")
        val stripMine = driver.putLandOnBattlefield(opponent, "Strip Mine")
        driver.giveColorlessMana(player, 1)

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = fault,
                abilityId = sacAbilityId,
                targets = listOf(ChosenTarget.Permanent(stripMine)),
            )
        ).isSuccess shouldBe true

        // Resolve the ability, declining the opponent's optional basic-land search.
        var safety = 0
        while (safety < 40) {
            when (val pending = driver.state.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(pending.playerId, false)
                is SelectCardsDecision -> driver.submitCardSelection(pending.playerId, emptyList())
                else -> if (driver.stackSize > 0) driver.bothPass() else break
            }
            safety++
        }

        driver.findPermanent(player, "Volatile Fault") shouldBe null
        driver.findPermanent(opponent, "Strip Mine") shouldBe null
        // Declined the fetch: no basic entered the battlefield.
        forestsControlledBy(driver, opponent) shouldBe 0
        // The Treasure is created regardless of the opponent's choice.
        treasuresControlledBy(driver, player) shouldBe 1
    }
})
