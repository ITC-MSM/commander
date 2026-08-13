package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.PearlLakeAncient
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Regression for a multi-card activation cost interrupted by CR 903.9b. */
class ActivatedAbilityReturnToHandCommanderTest : FunSpec({
    fun setup(): GameTestDriver = GameTestDriver().also { driver ->
        driver.registerCards(TestCards.all)
        driver.registerCard(PearlLakeAncient)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /**
     * A Commander replacement choice deliberately pauses an activation.  This
     * is not a successful *completed* action yet, so it must not use
     * [GameTestDriver.submitSuccess], which rejects pending decisions.
     */
    fun activateWithLandBounce(
        driver: GameTestDriver,
        player: com.wingedsheep.sdk.model.EntityId,
        ancient: com.wingedsheep.sdk.model.EntityId,
        abilityId: com.wingedsheep.sdk.scripting.AbilityId,
        lands: List<com.wingedsheep.sdk.model.EntityId>
    ) {
        val result = driver.submit(ActivateAbility(
            playerId = player,
            sourceId = ancient,
            abilityId = abilityId,
            costPayment = AdditionalCostPayment(bouncedPermanents = lands)
        ))
        result.error shouldBe null
        result.isPaused shouldBe true
    }

    test("three Commander land bounce costs serialize before Pearl Lake Ancient is stacked once") {
        val driver = setup()
        val player = driver.activePlayer!!
        val ancient = driver.putCreatureOnBattlefield(player, "Pearl Lake Ancient")
        val lands = List(3) { driver.putLandOnBattlefield(player, "Forest") }
        driver.replaceState(driver.state.copy(format = Format.Commander()).let { state ->
            lands.fold(state) { current, land ->
                current.updateEntity(land) { it.with(CommanderComponent(player)) }
            }
        })
        val abilityId = PearlLakeAncient.activatedAbilities.single().id

        activateWithLandBounce(driver, player, ancient, abilityId, lands)
        repeat(3) {
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            driver.state.stack.size shouldBe 0
            driver.submitYesNo(player, true)
        }
        lands.forEach { land ->
            driver.state.getZone(ZoneKey(player, Zone.COMMAND)).contains(land) shouldBe true
        }
        driver.state.stack.size shouldBe 1
    }

    test("declining each Commander land bounce cost returns all three to hand before stacking once") {
        val driver = setup()
        val player = driver.activePlayer!!
        val ancient = driver.putCreatureOnBattlefield(player, "Pearl Lake Ancient")
        val lands = List(3) { driver.putLandOnBattlefield(player, "Forest") }
        driver.replaceState(driver.state.copy(format = Format.Commander()).let { state ->
            lands.fold(state) { current, land ->
                current.updateEntity(land) { it.with(CommanderComponent(player)) }
            }
        })
        val abilityId = PearlLakeAncient.activatedAbilities.single().id

        activateWithLandBounce(driver, player, ancient, abilityId, lands)
        repeat(3) {
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            driver.state.stack.size shouldBe 0
            driver.submitYesNo(player, false)
        }
        lands.forEach { land ->
            driver.state.getZone(ZoneKey(player, Zone.HAND)).contains(land) shouldBe true
        }
        driver.state.stack.size shouldBe 1
    }
})
