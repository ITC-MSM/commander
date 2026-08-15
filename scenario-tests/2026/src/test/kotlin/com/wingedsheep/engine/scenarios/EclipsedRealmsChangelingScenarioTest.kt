package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ecl.cards.EclipsedRealms
import com.wingedsheep.mtg.sets.definitions.ecl.cards.FirdochCore
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Eclipsed Realms (ECL #263) — Land
 *
 *   {T}: Add one mana of any color. Spend this mana only to cast a spell of the chosen type
 *        or activate an ability of a source of the chosen type.
 *
 * Regression coverage for changeling (CR 702.73a — a card with changeling is every creature
 * type, in every zone). A Shapeshifter with changeling such as Firdoch Core (ECL #255) is an
 * Elf spell, a Goblin spell, … so Eclipsed Realms' restricted mana must be spendable on it
 * regardless of which type was chosen — both when casting it and when activating an ability
 * of it on the battlefield.
 */
class EclipsedRealmsChangelingScenarioTest : FunSpec({

    /** A plain 3-mana artifact creature with no creature type overlap and no changeling. */
    val testBear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = ManaCost.parse("{3}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(EclipsedRealms, FirdochCore, testBear))
        return driver
    }

    val anyColorAbilityId = EclipsedRealms.activatedAbilities[1].id
    val becomeCreatureAbilityId = FirdochCore.activatedAbilities[1].id

    /**
     * Put Eclipsed Realms on the battlefield with [chosenType] already chosen (the
     * "as this enters" replacement is exercised elsewhere), then tap it for one
     * restricted mana of [color].
     */
    fun GameTestDriver.tapEclipsedRealms(
        playerId: EntityId,
        chosenType: String,
        color: Color = Color.GREEN
    ) {
        val land = putPermanentOnBattlefield(playerId, "Eclipsed Realms")
        replaceState(state.updateEntity(land) { c ->
            c.with(CastChoicesComponent(chosen = mapOf(ChoiceSlot.CREATURE_TYPE to ChoiceValue.TextChoice(chosenType))))
        })
        submit(ActivateAbility(playerId, land, anyColorAbilityId))
        state.pendingDecision?.let { decision ->
            submitDecision(playerId, ColorChosenResponse(decision.id, color))
        }
    }

    test("restricted mana lands in the pool after tapping") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapEclipsedRealms(p1, "Elf")

        driver.state.getEntity(p1)?.get<ManaPoolComponent>()!!.restrictedMana.size shouldBe 1
    }

    test("chosen-type mana can cast a changeling spell of an unrelated printed type") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapEclipsedRealms(p1, "Elf")
        driver.giveColorlessMana(p1, 2)

        // Firdoch Core is a Kindred Artifact — Shapeshifter with changeling, so it is an Elf spell.
        val core = driver.putCardInHand(p1, "Firdoch Core")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = core, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe true
    }

    test("chosen-type mana cannot cast a spell that is not of the chosen type") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapEclipsedRealms(p1, "Elf")
        driver.giveColorlessMana(p1, 2)

        val bear = driver.putCardInHand(p1, "Test Bear")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = bear, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe false
    }

    test("chosen-type mana can activate an ability of a changeling source") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapEclipsedRealms(p1, "Goblin")
        driver.giveColorlessMana(p1, 3)

        val core = driver.putPermanentOnBattlefield(p1, "Firdoch Core")
        val result = driver.submit(
            ActivateAbility(p1, core, becomeCreatureAbilityId, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe true
    }
})
