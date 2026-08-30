package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.support.setupP1
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ForcedTargetEnumerationTest : FunSpec({

    fun testCard(optional: Boolean) = card(
        if (optional) "Optional Self Target" else "Mandatory Self Target"
    ) {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        activatedAbility {
            cost = Costs.Free
            target("creature", TargetCreature(optional = optional))
            effect = Effects.GainLife(1)
        }
    }

    fun sourceId(driver: com.wingedsheep.engine.legalactions.support.EnumerationTestDriver, name: String): EntityId =
        driver.game.state.getBattlefield(driver.player1).single { id ->
            driver.game.state.getEntity(id)?.get<CardComponent>()?.name == name
        }

    test("optional self target remains an explicit zero-or-one choice") {
        val card = testCard(optional = true)
        val driver = setupP1(battlefield = listOf(card.name), extraSetCards = listOf(card))
        val source = sourceId(driver, card.name)

        val actions = driver.enumerateFor(driver.player1).activatedAbilityActionsFor(source)
        actions.shouldHaveSize(1)
        val legalAction = actions.single()
        val action = legalAction.action as ActivateAbility

        action.targets shouldBe emptyList()
        legalAction.requiresTargets shouldBe true
        legalAction.minTargets shouldBe 0
        legalAction.validTargets.shouldContainExactly(source)
    }

    test("mandatory self target still auto-selects its only legal target") {
        val card = testCard(optional = false)
        val driver = setupP1(battlefield = listOf(card.name), extraSetCards = listOf(card))
        val source = sourceId(driver, card.name)

        val actions = driver.enumerateFor(driver.player1).activatedAbilityActionsFor(source)
        actions.shouldHaveSize(1)
        val legalAction = actions.single()
        val action = legalAction.action as ActivateAbility

        action.targets.shouldContainExactly(ChosenTarget.Permanent(source))
        legalAction.requiresTargets shouldBe false
    }
})
