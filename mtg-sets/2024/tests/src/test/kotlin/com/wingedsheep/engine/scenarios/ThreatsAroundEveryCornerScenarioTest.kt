package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.ThreatsAroundEveryCorner
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Threats Around Every Corner (DSK #200) — {3}{G} Enchantment.
 *
 * "When this enchantment enters, manifest dread.
 *  Whenever a face-down permanent you control enters, search your library for a basic land card,
 *  put it onto the battlefield tapped, then shuffle."
 *
 * Exercises: the manifest-dread ETB recipe, and an ANY-binding "a face-down permanent you control
 * enters" trigger that fires off the manifest-dread permanent itself and fetches a basic land
 * entering tapped.
 */
class ThreatsAroundEveryCornerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + ThreatsAroundEveryCorner)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.countTappedBasicLands(playerId: EntityId): Int =
        getPermanents(playerId).count { id ->
            val e = state.getEntity(id)
            e?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Forest" &&
                e.get<TappedComponent>() != null
        }

    test("ETB manifest dread makes a face-down permanent, which fetches a tapped basic land") {
        val d = newDriver()
        val you = d.player1

        // Top two cards for manifest dread: a creature on top (to manifest), a Forest beneath.
        d.putCardOnTopOfLibrary(you, "Forest")
        val creature = d.putCardOnTopOfLibrary(you, "Centaur Courser")

        val before = d.countTappedBasicLands(you)

        // Cast Threats Around Every Corner.
        val enchant = d.putCardInHand(you, "Threats Around Every Corner")
        d.giveMana(you, Color.GREEN, 4)
        d.castSpell(you, enchant)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // First decision: manifest-dread pick — manifest the creature.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(creature)))

        // Resolve everything: the face-down permanent entering fires the second ability, which
        // searches for a basic land. With only basic Forests available the search auto-finds one;
        // satisfy any library-search prompt that appears.
        var guard = 0
        while (guard++ < 20 && !(d.state.stack.isEmpty() && d.state.pendingDecision == null)) {
            val pending = d.pendingDecision
            if (pending is SelectCardsDecision && pending.options.isNotEmpty()) {
                d.submitDecision(you, CardsSelectedResponse(decisionId = pending.id, selectedCards = listOf(pending.options.first())))
            } else {
                d.bothPass()
            }
        }

        // The manifested card is a face-down permanent.
        d.state.getEntity(creature)?.get<FaceDownComponent>() shouldBe FaceDownComponent

        // A basic land was fetched onto the battlefield tapped (net +1 over the starting count).
        d.countTappedBasicLands(you) shouldBe before + 1
    }
})
