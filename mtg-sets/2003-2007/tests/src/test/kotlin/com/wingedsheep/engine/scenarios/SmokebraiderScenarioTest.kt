package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Smokebraider
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Smokebraider (LRW #189) — {1}{R} Creature — Elemental Shaman 1/1
 *
 *   {T}: Add two mana in any combination of colors. Spend this mana only to cast Elemental
 *        spells or activate abilities of Elementals.
 *
 * Two mana, each pip coloured independently, both carrying
 * `ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Elemental")`. The tests pin the *count* (two, not
 * one), the restriction admitting an Elemental spell, and the restriction refusing everything else.
 */
class SmokebraiderScenarioTest : FunSpec({

    val testElemental = CardDefinition.creature(
        name = "Test Elemental",
        manaCost = ManaCost.parse("{W}{U}"),
        subtypes = setOf(Subtype("Elemental")),
        power = 2,
        toughness = 2
    )
    val testGoblin = CardDefinition.creature(
        name = "Test Goblin",
        manaCost = ManaCost.parse("{W}{U}"),
        subtypes = setOf(Subtype("Goblin")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + Smokebraider + testElemental + testGoblin)
        return driver
    }

    val manaAbilityId = Smokebraider.activatedAbilities[0].id

    /**
     * "In any combination of colors" prompts per pip, so the driver answers the colour decision
     * until the ability stops asking — one white, then one blue, matching the two-colour test
     * spells below.
     */
    fun GameTestDriver.tapForElementalMana(playerId: EntityId) {
        val smokebraider = putCreatureOnBattlefield(playerId, "Smokebraider")
        removeSummoningSickness(smokebraider)
        submit(ActivateAbility(playerId, smokebraider, manaAbilityId))
        val colors = listOf(Color.WHITE, Color.BLUE)
        var next = 0
        while (state.pendingDecision != null && next < colors.size) {
            val decision = state.pendingDecision!!
            submitDecision(playerId, ColorChosenResponse(decision.id, colors[next]))
            next++
        }
    }

    test("tapping adds two restricted mana, not one") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForElementalMana(p1)

        val pool = driver.state.getEntity(p1)?.get<ManaPoolComponent>()
        withClue("Smokebraider is the two-mana Springleaf Drum of Elementals, not a one-mana dork") {
            pool!!.restrictedMana.size shouldBe 2
        }
    }

    test("the restricted mana pays for an Elemental spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForElementalMana(p1)

        val elemental = driver.putCardInHand(p1, "Test Elemental")
        driver.submit(
            CastSpell(playerId = p1, cardId = elemental, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
    }

    test("the restricted mana cannot pay for a non-Elemental spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForElementalMana(p1)

        val goblin = driver.putCardInHand(p1, "Test Goblin")
        driver.submit(
            CastSpell(playerId = p1, cardId = goblin, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe false
    }
})
