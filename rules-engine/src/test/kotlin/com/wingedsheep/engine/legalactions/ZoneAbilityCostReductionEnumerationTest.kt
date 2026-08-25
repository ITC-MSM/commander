package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.legalactions.support.EnumerationTestDriver
import com.wingedsheep.engine.legalactions.support.setupP1
import com.wingedsheep.engine.legalactions.support.shouldContainActivatedAbilityOn
import com.wingedsheep.engine.legalactions.support.shouldNotContainActivatedAbilityOn
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * [enumerators.ZoneActivatedAbilityEnumerator] and
 * [com.wingedsheep.sdk.scripting.ActivatedAbility.genericCostReduction] together — a cost
 * reduction on an ability activated from a *non-battlefield* zone.
 *
 * Regression. The reduction used to be applied in only two places: the battlefield enumerator
 * and [com.wingedsheep.engine.handlers.actions.ability.ActivateAbilityHandler]. The zone
 * enumerator gated affordability on the *unreduced* cost, so a from-hand ability that the
 * handler would have been happy to charge the reduced price for was never offered as a legal
 * action — unreachable in a real game. Both now route through
 * [com.wingedsheep.engine.legalactions.utils.AbilityCostReduction].
 *
 * The fixture is Otawara, Soaring City, whose channel ability is activated by discarding it from
 * hand and "costs {1} less to activate for each legendary creature you control". The other four
 * NEO channel lands share the shape; each has its own scenario test alongside its card.
 */
class ZoneAbilityCostReductionEnumerationTest : FunSpec({

    /** Distinct legendary creatures, so the legend rule never eats the count being tested. */
    fun legend(name: String): CardDefinition = CardDefinition.creature(
        name = name,
        manaCost = ManaCost.parse("{1}"),
        subtypes = setOf(Subtype("Spirit")),
        power = 1,
        toughness = 1,
        supertypes = setOf(Supertype.LEGENDARY)
    )

    val legends = listOf("Test Legend Alpha", "Test Legend Beta", "Test Legend Gamma")
    val legendCards = legends.map { legend(it) }

    fun handId(driver: EnumerationTestDriver, name: String): EntityId {
        val state = driver.game.state
        return state.getZone(driver.player1, Zone.HAND).first { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == name
        }
    }

    fun otawaraCost(driver: EnumerationTestDriver): String? {
        val id = handId(driver, "Otawara, Soaring City")
        return driver.enumerateFor(driver.player1)
            .activatedAbilityActionsFor(id).single().manaCostString
    }

    context("Otawara, Soaring City — Channel {3}{U}, costs {1} less per legendary creature") {

        test("no legendary creatures — full {3}{U}, and four lands pay it") {
            val driver = setupP1(
                hand = listOf("Otawara, Soaring City"),
                // Four Islands for the unreduced cost, plus a nonlegendary creature to target.
                battlefield = listOf("Island", "Island", "Island", "Island", "Grizzly Bears"),
                atStep = Step.PRECOMBAT_MAIN
            )
            val otawara = handId(driver, "Otawara, Soaring City")

            driver.enumerateFor(driver.player1) shouldContainActivatedAbilityOn otawara
            otawaraCost(driver) shouldBe "{3}{U}"
        }

        test("two legendary creatures — reduced to {1}{U}, and two lands are enough") {
            val driver = setupP1(
                hand = listOf("Otawara, Soaring City"),
                // Only two Islands: {3}{U} is unpayable, {1}{U} is payable.
                battlefield = listOf("Island", "Island", legends[0], legends[1]),
                extraSetCards = legendCards,
                atStep = Step.PRECOMBAT_MAIN
            )
            val otawara = handId(driver, "Otawara, Soaring City")

            withClue("Before the shared reduction, affordability was gated on the full {3}{U}") {
                driver.enumerateFor(driver.player1) shouldContainActivatedAbilityOn otawara
            }
            withClue("…and the menu must show what the handler will actually charge") {
                otawaraCost(driver) shouldBe "{1}{U}"
            }
        }

        test("three legendary creatures — the coloured pip survives, floor is {U}") {
            val driver = setupP1(
                hand = listOf("Otawara, Soaring City"),
                battlefield = listOf("Island", legends[0], legends[1], legends[2]),
                extraSetCards = legendCards,
                atStep = Step.PRECOMBAT_MAIN
            )
            val otawara = handId(driver, "Otawara, Soaring City")

            driver.enumerateFor(driver.player1) shouldContainActivatedAbilityOn otawara
            withClue("Reduction is generic-only — three legends can't eat the {U}") {
                otawaraCost(driver) shouldBe "{U}"
            }
        }

        test("two legendary creatures but no blue source — still not offered") {
            val driver = setupP1(
                hand = listOf("Otawara, Soaring City"),
                // Forests can pay the generic remainder but never the {U}.
                battlefield = listOf("Forest", "Forest", legends[0], legends[1]),
                extraSetCards = legendCards,
                atStep = Step.PRECOMBAT_MAIN
            )
            val otawara = handId(driver, "Otawara, Soaring City")

            withClue("The reduction lowers the cost; it does not make it colourless") {
                driver.enumerateFor(driver.player1) shouldNotContainActivatedAbilityOn otawara
            }
        }
    }
})
