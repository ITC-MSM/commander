package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.woe.cards.AgathasSoulCauldron
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Agatha's Soul Cauldron — {2} Legendary Artifact (WOE #242).
 *
 *  - "{T}: Exile target card from a graveyard. When a creature card is exiled this way, put a
 *    +1/+1 counter on target creature you control."
 *  - "Creatures you control with +1/+1 counters on them have all activated abilities of all
 *    creature cards exiled with Agatha's Soul Cauldron."
 *  - "You may spend mana as though it were mana of any color to activate abilities of creatures
 *    you control."
 */
class AgathasSoulCauldronScenarioTest : FunSpec({

    // A creature card whose only ability has a COLORED cost — used to prove both the ability
    // granting (its {R} ability lands on a counter-creature) and the mana permission (the {R}
    // is payable with off-color mana).
    val lifeMystic = card("Cauldron Test Mystic") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        oracleText = "{R}: You gain 1 life."
        activatedAbility {
            cost = Costs.Mana("{R}")
            effect = Effects.GainLife(1)
        }
    }
    val mysticAbilityId = lifeMystic.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AgathasSoulCauldron, lifeMystic))
        return driver
    }

    fun counters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("exile a creature card → reflexive +1/+1 counter → counter-creature gains and activates the exiled ability with off-color mana") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cauldron = driver.putPermanentOnBattlefield(me, "Agatha's Soul Cauldron")
        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val mysticInGrave = driver.putCardInGraveyard(me, "Cauldron Test Mystic")

        // Activate "{T}: Exile target card from a graveyard." targeting the creature card.
        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = cauldron,
                abilityId = AgathasSoulCauldron.activatedAbilities[0].id,
                targets = listOf(ChosenTarget.Card(mysticInGrave, ownerId = me, zone = Zone.GRAVEYARD))
            )
        )
        // Resolve the ability: exile the mystic, then the reflexive trigger (creature card → true)
        // auto-targets the only creature you control (Grizzly Bears) and adds a +1/+1 counter.
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        driver.state.getZone(me, Zone.EXILE).contains(mysticInGrave) shouldBe true
        counters(driver, bears) shouldBe 1

        // The Bears now has a +1/+1 counter, so it has the exiled creature's "{R}: gain 1 life".
        // Pay the {R} with GREEN mana — Agatha lets you spend mana as any color for your creatures.
        driver.giveMana(me, Color.GREEN, 1)
        val lifeBefore = driver.getLifeTotal(me)
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = bears, abilityId = mysticAbilityId))
        guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        driver.getLifeTotal(me) shouldBe lifeBefore + 1
    }

    test("a creature WITHOUT a +1/+1 counter does not gain the exiled creature's abilities") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cauldron = driver.putPermanentOnBattlefield(me, "Agatha's Soul Cauldron")
        // Two creatures: one will receive the reflexive counter, the other stays counter-less.
        val withCounter = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val withoutCounter = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val mysticInGrave = driver.putCardInGraveyard(me, "Cauldron Test Mystic")

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = cauldron,
                abilityId = AgathasSoulCauldron.activatedAbilities[0].id,
                targets = listOf(ChosenTarget.Card(mysticInGrave, ownerId = me, zone = Zone.GRAVEYARD))
            )
        )
        // Two eligible creatures → the reflexive trigger pauses to choose where the counter goes.
        var guard = 0
        while (driver.state.pendingDecision == null && driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()
        driver.submitTargetSelection(me, listOf(withCounter)).isSuccess shouldBe true
        guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        counters(driver, withCounter) shouldBe 1
        counters(driver, withoutCounter) shouldBe 0

        // The counter-less creature has no granted ability — activating it fails.
        driver.giveMana(me, Color.RED, 1)
        driver.submit(ActivateAbility(playerId = me, sourceId = withoutCounter, abilityId = mysticAbilityId))
            .isSuccess shouldBe false
    }

    test("exiling a non-creature card adds no counter (reflexive trigger does not fire)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cauldron = driver.putPermanentOnBattlefield(me, "Agatha's Soul Cauldron")
        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val landInGrave = driver.putCardInGraveyard(me, "Forest")

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = cauldron,
                abilityId = AgathasSoulCauldron.activatedAbilities[0].id,
                targets = listOf(ChosenTarget.Card(landInGrave, ownerId = me, zone = Zone.GRAVEYARD))
            )
        )
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        // The land is exiled, but it is not a creature card, so no counter is placed.
        driver.state.getZone(me, Zone.EXILE).contains(landInGrave) shouldBe true
        counters(driver, bears) shouldBe 0
    }
})
