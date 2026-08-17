package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.legalactions.support.EnumerationFixtures
import com.wingedsheep.engine.legalactions.support.EnumerationTestDriver
import com.wingedsheep.engine.legalactions.support.setupP1
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Tests for [enumerators.ManaAbilityEnumerator] over the simplest case:
 * basic-land tap-for-mana abilities. More exotic mana costs (sacrifice,
 * tap-permanents, composite) are covered in the ActivatedAbilityEnumerator
 * tests in phase 6.
 */
class ManaAbilityEnumeratorTest : FunSpec({

    test("an untapped Forest produces a tap-for-{G} mana ability") {
        val driver = EnumerationFixtures.allForestsMainPhase()
        val forestId = driver.game.state.getHand(driver.player1).first()
        driver.game.playLand(driver.player1, forestId)

        val manaAbilities = driver.enumerateFor(driver.player1)
            .activatedAbilityActionsFor(forestId)

        manaAbilities shouldHaveSize 1
        val tap = manaAbilities.single()
        tap.isManaAbility shouldBe true
        tap.affordable shouldBe true
        tap.manaCostString shouldBe null  // Tap cost has no mana component
    }

    test("a tapped Forest still appears as an ability but is marked unaffordable") {
        val driver = EnumerationFixtures.allForestsMainPhase()
        val forestId = driver.game.state.getHand(driver.player1).first()
        driver.game.playLand(driver.player1, forestId)

        // Directly tap the Forest by mutating its components.
        val container = driver.game.state.getEntity(forestId)!!
        val tapped = container.with(
            com.wingedsheep.engine.state.components.battlefield.TappedComponent
        )
        driver.game.replaceState(driver.game.state.withEntity(forestId, tapped))

        val manaAbilities = driver.enumerateFor(driver.player1)
            .activatedAbilityActionsFor(forestId)

        manaAbilities shouldHaveSize 1
        manaAbilities.single().affordable shouldBe false
    }

    test("opponent's lands do not produce mana abilities for me") {
        val driver = EnumerationFixtures.allForestsMainPhase()
        val forestId = driver.game.state.getHand(driver.player1).first()
        driver.game.playLand(driver.player1, forestId)

        // Player 2 should see zero mana abilities on Player 1's Forest.
        val opponentView = driver.enumerateFor(driver.player2)
            .activatedAbilityActionsFor(forestId)

        opponentView.shouldBeEmpty()
    }

    test("with no permanents on the battlefield there are no mana abilities") {
        val driver = EnumerationFixtures.allForestsMainPhase()

        val abilities = driver.enumerateFor(driver.player1).activatedAbilityActions()

        abilities.shouldBeEmpty()
    }

    // -------------------------------------------------------------------------
    // Activation restrictions on mana abilities (Evendo, Waking Haven's 12+
    // charge-counter gate). The enumerator must hide the ability when an
    // ActivationRestriction.OnlyIfCondition isn't satisfied — same pattern
    // ActivatedAbilityEnumerator uses for Weathered Wayfarer.
    val ConditionalManaLand = card("Test Conditional Mana Land") {
        typeLine = "Land"
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddMana(Color.GREEN)
            manaAbility = true
            restrictions = listOf(
                ActivationRestriction.OnlyIfCondition(Conditions.LifeAtLeast(50))
            )
        }
    }

    test("OnlyIfCondition unmet — restricted mana ability is NOT enumerated") {
        // Default life is 20, threshold is 50 → restriction fails.
        val driver = setupP1(
            battlefield = listOf("Test Conditional Mana Land"),
            extraSetCards = listOf(ConditionalManaLand)
        )
        val landId = driver.game.state.getBattlefield(driver.player1).first { id ->
            driver.game.state.getEntity(id)?.get<CardComponent>()?.name == "Test Conditional Mana Land"
        }

        driver.enumerateFor(driver.player1).activatedAbilityActionsFor(landId).shouldBeEmpty()
    }

    test("OnlyIfCondition met — restricted mana ability surfaces as an activatable action") {
        val driver = setupP1(
            battlefield = listOf("Test Conditional Mana Land"),
            extraSetCards = listOf(ConditionalManaLand)
        )
        val landId = driver.game.state.getBattlefield(driver.player1).first { id ->
            driver.game.state.getEntity(id)?.get<CardComponent>()?.name == "Test Conditional Mana Land"
        }

        // Bump P1's life total above the threshold so the condition holds.
        var state = driver.game.state
        val p1 = state.getEntity(driver.player1)!!.with(LifeTotalComponent(50))
        state = state.withEntity(driver.player1, p1)
        driver.game.replaceState(state)

        val abilities = driver.enumerateFor(driver.player1).activatedAbilityActionsFor(landId)
        abilities shouldHaveSize 1
        abilities.single().isManaAbility shouldBe true
        abilities.single().affordable shouldBe true
    }

    // -------------------------------------------------------------------------
    // CostAtom.TapPermanents.excludeSelf — the enumerated pool must be the one
    // payment validation accepts. Enumeration used to offer every matching
    // untapped permanent, so an "another"-shaped mana ability was reported legal
    // with only the source untapped, and listed the source in validTapTargets
    // for a tap the payment path then rejected (issue #1880).

    val TapAnotherManaCreature = card("Test Tap-Another Mana Creature") {
        typeLine = "Creature — Elf Druid"
        power = 1; toughness = 1
        activatedAbility {
            cost = Costs.TapAnotherPermanent(GameObjectFilter.Creature)
            effect = Effects.AddMana(Color.GREEN)
            manaAbility = true
        }
    }

    val TapAnyManaCreature = card("Test Tap-Any Mana Creature") {
        typeLine = "Creature — Elf Druid"
        power = 1; toughness = 1
        activatedAbility {
            cost = Costs.TapPermanents(1, GameObjectFilter.Creature)
            effect = Effects.AddMana(Color.GREEN)
            manaAbility = true
        }
    }

    fun bf(driver: EnumerationTestDriver, name: String) =
        driver.game.state.getBattlefield(driver.player1).first { id ->
            driver.game.state.getEntity(id)?.get<CardComponent>()?.name == name
        }

    test("excludeSelf=false — the source is a legal tap target for its own mana ability") {
        val driver = setupP1(
            battlefield = listOf("Test Tap-Any Mana Creature"),
            extraSetCards = listOf(TapAnyManaCreature)
        )
        val sourceId = bf(driver, "Test Tap-Any Mana Creature")

        val abilities = driver.enumerateFor(driver.player1).activatedAbilityActionsFor(sourceId)
        abilities shouldHaveSize 1
        abilities.single().affordable shouldBe true
        abilities.single().additionalCostInfo?.validTapTargets shouldBe listOf(sourceId)
    }

    test("excludeSelf=true — the source alone doesn't make the mana ability affordable") {
        val driver = setupP1(
            battlefield = listOf("Test Tap-Another Mana Creature"),
            extraSetCards = listOf(TapAnotherManaCreature)
        )
        val sourceId = bf(driver, "Test Tap-Another Mana Creature")

        val abilities = driver.enumerateFor(driver.player1).activatedAbilityActionsFor(sourceId)
        abilities shouldHaveSize 1
        abilities.single().affordable shouldBe false
        abilities.single().additionalCostInfo?.validTapTargets?.shouldBeEmpty()
    }

    test("excludeSelf=true — validTapTargets offers the other creature, never the source") {
        val driver = setupP1(
            battlefield = listOf("Test Tap-Another Mana Creature", "Test Tap-Any Mana Creature"),
            extraSetCards = listOf(TapAnotherManaCreature, TapAnyManaCreature)
        )
        val sourceId = bf(driver, "Test Tap-Another Mana Creature")
        val otherId = bf(driver, "Test Tap-Any Mana Creature")

        val ability = driver.enumerateFor(driver.player1)
            .activatedAbilityActionsFor(sourceId).single()
        ability.affordable shouldBe true
        ability.additionalCostInfo?.validTapTargets shouldBe listOf(otherId)
    }

    test("two Forests in play produce two distinct mana ability actions") {
        // Place two Forests on the battlefield via state surgery. Going through
        // two full turn cycles to play two lands legally would distract from
        // what's being tested — that the enumerator scans every controlled
        // permanent, not just one.
        val driver = EnumerationFixtures.allForestsMainPhase()

        val handIds = driver.game.state.getHand(driver.player1)
        val forest1 = handIds[0]
        val forest2 = handIds[1]

        var state = driver.game.state
        for (forestId in listOf(forest1, forest2)) {
            state = state.moveToZone(
                forestId,
                from = com.wingedsheep.engine.state.ZoneKey(driver.player1, com.wingedsheep.sdk.core.Zone.HAND),
                to = com.wingedsheep.engine.state.ZoneKey(driver.player1, com.wingedsheep.sdk.core.Zone.BATTLEFIELD)
            )
        }
        driver.game.replaceState(state)

        val mineActions = driver.enumerateFor(driver.player1).activatedAbilityActions()

        mineActions shouldHaveSize 2
        mineActions.map { (it.action as com.wingedsheep.engine.core.ActivateAbility).sourceId }
            .toSet() shouldBe setOf(forest1, forest2)
    }
})
