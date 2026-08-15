package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.IgnisScientia
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ignis Scientia — {1}{G}{U} Legendary Creature — Human Advisor, 2/2
 *
 *   ETB: "look at the top six cards of your library. You may put a land card from among them
 *         onto the battlefield tapped. Put the rest on the bottom of your library in a random order."
 *   {1}{G}{U}, {T}: "Exile target card from a graveyard. If a creature card was exiled this way,
 *                    create a Food token."
 *
 * The card is pure composition over existing pipeline primitives (GatherCards / SelectFromCollection /
 * MoveCollection / ConditionalEffect + CollectionContainsMatch), so this test is the behavioural gate.
 */
class IgnisScientiaScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(IgnisScientia)
        driver.registerCard(PredefinedTokens.Food) // GameTestDriver doesn't auto-register tokens
        return driver
    }

    fun countFood(driver: GameTestDriver, playerId: EntityId): Int =
        driver.getPermanents(playerId).count { entityId ->
            driver.state.getEntity(entityId)?.get<CardComponent>()?.name == "Food"
        }

    val abilityId = IgnisScientia.activatedAbilities[0].id

    test("activated ability: exiling a creature card from a graveyard creates a Food token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ignis = driver.putCreatureOnBattlefield(p1, "Ignis Scientia")
        driver.removeSummoningSickness(ignis) // {T} cost needs it not summoning-sick

        // A creature card in a graveyard to exile.
        val bearInGy = driver.putCardInGraveyard(p1, "Grizzly Bears")

        // Pay {1}{G}{U}.
        driver.giveMana(p1, Color.GREEN, 2)
        driver.giveMana(p1, Color.BLUE, 1)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = ignis,
                abilityId = abilityId,
                targets = listOf(entityIdToChosenTarget(driver.state, bearInGy))
            )
        )
        withClue("error=${result.error}") { result.isSuccess shouldBe true }
        driver.bothPass()

        // The creature card is exiled and a Food token was created.
        driver.getExile(p1).contains(bearInGy) shouldBe true
        countFood(driver, p1) shouldBe 1
    }

    test("activated ability: exiling a non-creature card creates no Food token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ignis = driver.putCreatureOnBattlefield(p1, "Ignis Scientia")
        driver.removeSummoningSickness(ignis)

        // A land card (non-creature) in the graveyard.
        val landInGy = driver.putCardInGraveyard(p1, "Forest")

        driver.giveMana(p1, Color.GREEN, 2)
        driver.giveMana(p1, Color.BLUE, 1)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = ignis,
                abilityId = abilityId,
                targets = listOf(entityIdToChosenTarget(driver.state, landInGy))
            )
        )
        withClue("error=${result.error}") { result.isSuccess shouldBe true }
        driver.bothPass()

        driver.getExile(p1).contains(landInGy) shouldBe true
        countFood(driver, p1) shouldBe 0
    }

    test("ETB: puts a land onto the battlefield tapped, bottoming the rest") {
        val driver = createDriver()
        // All-Forest library guarantees a land is among the top six looked at.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val landsBefore = driver.getLands(p1).size

        // Cast Ignis from hand so its enters trigger actually fires (direct placement helpers don't).
        val ignis = driver.putCardInHand(p1, "Ignis Scientia")
        driver.giveMana(p1, Color.GREEN, 2)
        driver.giveMana(p1, Color.BLUE, 1)
        driver.castSpell(p1, ignis)

        // Resolve until the ETB trigger pauses for the "you may" land selection.
        var guard = 0
        while (driver.pendingDecision == null && driver.stackSize > 0 && guard++ < 20) {
            driver.bothPass()
        }

        val decision = driver.pendingDecision
        withClue("Ignis ETB should prompt a land selection, got $decision") {
            (decision is SelectCardsDecision) shouldBe true
        }
        decision as SelectCardsDecision
        driver.submitCardSelection(p1, listOf(decision.options.first()))
        driver.bothPass()

        // A land entered the battlefield (tapped) from the top six.
        withClue("Ignis ETB should have put one Forest onto the battlefield") {
            driver.getLands(p1).size shouldBe landsBefore + 1
        }
        val newLand = driver.getLands(p1).first { driver.state.getEntity(it)?.get<CardComponent>()?.name == "Forest" }
        withClue("The land Ignis puts onto the battlefield enters tapped") {
            driver.isTapped(newLand) shouldBe true
        }
    }
})
