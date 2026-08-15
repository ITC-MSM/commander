package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.FaramirFieldCommander
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Faramir, Field Commander — exercises the `YouChoseOtherCreatureAsRingBearer` intervening-if
 * (Gap 3, CR 701.54a). The card-level test confirms the new condition fires the token trigger
 * only when the player picked a Ring-bearer other than Faramir, while the end-step draw half
 * (composed from existing `Conditions.ControlledCreatureDiedThisTurn`) is covered by the
 * companion mechanic test suite.
 */
class FaramirFieldCommanderTest : FunSpec({

    val RingTempter = card("Ring Tempter") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "The Ring tempts you."
        spell { effect = Effects.TheRingTemptsYou() }
    }

    val Bear = CardDefinition.creature("Ring Bear", ManaCost.parse("{2}"), emptySet(), 2, 2)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RingTempter, Bear, FaramirFieldCommander))
        return driver
    }

    fun GameTestDriver.tempt(player: EntityId, bearerId: EntityId?) {
        val cardId = putCardInHand(player, "Ring Tempter")
        castSpell(player, cardId)
        bothPass()
        val decision = pendingDecision
        if (decision is SelectCardsDecision && bearerId != null) {
            submitDecision(player, CardsSelectedResponse(decision.id, listOf(bearerId)))
        }
    }

    fun GameTestDriver.countSoldierTokens(playerId: EntityId): Int {
        val projected = state.projectedState
        return state.getBattlefield().count { id ->
            projected.getController(id) == playerId &&
                projected.isCreature(id) &&
                projected.hasSubtype(id, "Soldier")
        }
    }

    test("triggers and creates a Soldier when you choose a creature other than Faramir") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Faramir, Field Commander")
        val bear = driver.putCreatureOnBattlefield(active, "Ring Bear")
        val before = driver.countSoldierTokens(active)

        driver.tempt(active, bear)
        driver.bothPass() // resolve Faramir's "Whenever the Ring tempts you" trigger

        driver.countSoldierTokens(active) shouldBe before + 1
    }

    test("does NOT trigger when you choose Faramir as your Ring-bearer") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val faramir = driver.putCreatureOnBattlefield(active, "Faramir, Field Commander")
        driver.putCreatureOnBattlefield(active, "Ring Bear") // another option, but we pick Faramir
        val before = driver.countSoldierTokens(active)

        driver.tempt(active, faramir)
        driver.bothPass()

        driver.countSoldierTokens(active) shouldBe before
    }

    test("does NOT trigger when Faramir is your only creature, so you must choose him") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val faramir = driver.putCreatureOnBattlefield(active, "Faramir, Field Commander")
        val before = driver.countSoldierTokens(active)

        // Faramir is the only creature, so the only Ring-bearer choice is Faramir himself.
        driver.tempt(active, faramir)
        driver.bothPass()

        driver.countSoldierTokens(active) shouldBe before
    }
})
