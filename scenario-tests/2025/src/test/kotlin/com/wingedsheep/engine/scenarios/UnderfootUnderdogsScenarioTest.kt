package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.UnderfootUnderdogs
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Underfoot Underdogs (Tarkir: Dragonstorm):
 *  - "When this creature enters, create a 1/1 red Goblin creature token."
 *  - "{1}, {T}: Target creature you control with power 2 or less can't be blocked this turn."
 *
 * The ETB uses CreateTokenEffect; the activated ability composes Tap + {1} with a
 * GrantKeywordEffect(CANT_BE_BLOCKED) on a power-2-or-less, you-control target — both
 * existing primitives.
 */
class UnderfootUnderdogsScenarioTest : FunSpec({

    val unblockAbilityId = UnderfootUnderdogs.activatedAbilities.first().id

    // A vanilla 3/3 — too big to be a legal target for the unblockable ability.
    val bigBeast = CardDefinition.creature("Test Big Beast", ManaCost.parse("{2}{G}"), emptySet(), 3, 3)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(UnderfootUnderdogs)
        driver.registerCard(bigBeast)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun goblinTokenCount(driver: GameTestDriver, controller: EntityId): Int =
        driver.state.getBattlefield().count { id ->
            val card = driver.state.getEntity(id)?.get<CardComponent>()
            card?.name == "Goblin Token" &&
                driver.state.getEntity(id)?.get<ControllerComponent>()?.playerId == controller
        }

    test("entering creates a 1/1 red Goblin token") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        goblinTokenCount(driver, player) shouldBe 0

        val card = driver.putCardInHand(player, "Underfoot Underdogs")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, card)
        driver.bothPass() // resolve the spell — creature enters
        driver.bothPass() // resolve the ETB trigger — token created

        goblinTokenCount(driver, player) shouldBe 1
    }

    test("{1}, {T} grants CANT_BE_BLOCKED to a power-2-or-less creature you control") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val underdogs = driver.putCreatureOnBattlefield(player, "Underfoot Underdogs")
        driver.removeSummoningSickness(underdogs) // can pay the {T} cost
        driver.giveColorlessMana(player, 1)

        driver.state.projectedState.hasKeyword(underdogs, AbilityFlag.CANT_BE_BLOCKED) shouldBe false

        val result = driver.submit(
            ActivateAbility(player, underdogs, unblockAbilityId, targets = listOf(ChosenTarget.Permanent(underdogs)))
        )
        result.isSuccess shouldBe true
        driver.bothPass() // resolve the ability

        driver.state.projectedState.hasKeyword(underdogs, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
    }

    test("a power-3 creature is not a legal target for the unblockable ability") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val underdogs = driver.putCreatureOnBattlefield(player, "Underfoot Underdogs")
        driver.removeSummoningSickness(underdogs)
        val beast = driver.putCreatureOnBattlefield(player, "Test Big Beast")
        driver.giveColorlessMana(player, 1)

        driver.submitExpectFailure(
            ActivateAbility(player, underdogs, unblockAbilityId, targets = listOf(ChosenTarget.Permanent(beast)))
        )
        driver.state.projectedState.hasKeyword(beast, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
    }
})
