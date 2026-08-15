package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CanonizedInBlood
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Canonized in Blood (LCI #96): {1}{B} Enchantment
 *
 * 1. At the beginning of your end step, if you descended this turn, put a +1/+1 counter on
 *    target creature you control.
 * 2. {5}{B}{B}, Sacrifice this enchantment: Create a 4/3 white and black Vampire Demon creature
 *    token with flying.
 *
 * Tests:
 * 1. No counter is placed at the end step when the controller has not descended.
 * 2. A +1/+1 counter is placed on the targeted creature when descended this turn.
 * 3. Paying {5}{B}{B} and sacrificing the enchantment creates a 4/3 flying Vampire Demon token
 *    and removes the enchantment from the battlefield.
 */
class CanonizedInBloodScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CanonizedInBlood))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    /**
     * Move a permanent card to the graveyard to trigger descend (CR 700.11).
     */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.vampireDemonTokens(playerId: EntityId): List<EntityId> =
        getCreatures(playerId).filter { getCardName(it) == "Vampire Demon Token" }

    // -------------------------------------------------------------------------
    // Test 1: trigger does not fire if not descended
    // -------------------------------------------------------------------------
    test("no counter is placed at end step when the controller has not descended") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Canonized in Blood")
        val bear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        val countersBefore = plusCounters(driver, bear)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        // Trigger should not have fired — no ChooseTargetsDecision pending.
        (driver.pendingDecision is ChooseTargetsDecision) shouldBe false
        plusCounters(driver, bear) shouldBe countersBefore
    }

    // -------------------------------------------------------------------------
    // Test 2: trigger fires and puts a +1/+1 counter on the targeted creature
    // -------------------------------------------------------------------------
    test("descended this turn: puts a +1/+1 counter on target creature at end step") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Canonized in Blood")
        val bear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        // Descend: move a permanent card (creature in hand) to the graveyard.
        val handBear = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(handBear)

        val countersBefore = plusCounters(driver, bear)

        // Advance to end step; the trigger fires and requests a target.
        driver.passPriorityUntil(Step.END)
        var safety = 0
        while (safety++ < 20) {
            when {
                driver.pendingDecision is ChooseTargetsDecision ->
                    driver.submitTargetSelection(player, listOf(bear))
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
        }

        plusCounters(driver, bear) shouldBe countersBefore + 1
    }

    // -------------------------------------------------------------------------
    // Test 3: activated ability — sacrifice enchantment, create Vampire Demon token
    // -------------------------------------------------------------------------
    test("{5}{B}{B} + sacrifice: creates a 4/3 flying Vampire Demon token and removes the enchantment") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchantment = driver.putPermanentOnBattlefield(player, "Canonized in Blood")
        val tokensBefore = driver.vampireDemonTokens(player).size

        // Pay {5}{B}{B} — 5 colorless + 2 black.
        driver.giveColorlessMana(player, 5)
        driver.giveMana(player, Color.BLACK, 2)

        val abilityId = driver.cardRegistry.requireCard("Canonized in Blood").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(playerId = player, sourceId = enchantment, abilityId = abilityId)
        )
        driver.bothPass() // let the ability resolve

        // The enchantment is sacrificed as part of the cost.
        driver.findPermanent(player, "Canonized in Blood") shouldBe null

        // A 4/3 Vampire Demon token with flying is created.
        val tokensAfter = driver.vampireDemonTokens(player)
        tokensAfter.size shouldBe tokensBefore + 1

        val token = tokensAfter.first()
        driver.state.projectedState.getPower(token) shouldBe 4
        driver.state.projectedState.getToughness(token) shouldBe 3
        driver.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true

        // The token is white and black.
        driver.state.projectedState.hasColor(token, Color.WHITE) shouldBe true
        driver.state.projectedState.hasColor(token, Color.BLACK) shouldBe true
    }
})
