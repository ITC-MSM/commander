package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.PitOfOfferings
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Pit of Offerings (LCI #278) — Land — Cave.
 *
 *   This land enters tapped.
 *   When this land enters, exile up to three target cards from graveyards.
 *   {T}: Add {C}.
 *   {T}: Add one mana of any of the exiled cards' colors.
 *
 * Covers the new `ManaColorSet.AmongLinkedExiledCards` mana-color pool: the fourth ability
 * offers exactly the union of the base colors of the cards still exiled *with* this land (its
 * `LinkedExileComponent`, set by the ETB trigger's `MoveToZoneEffect(linkToSource = true)`).
 * Edge cases: colorless-only pile, empty pile, and a card that has since left exile all shrink
 * the pool. Plus the full ETB flow — exiling across multiple graveyards and linking the cards.
 */
class PitOfOfferingsScenarioTest : FunSpec({

    val whiteCreature = CardDefinition.creature(
        name = "White Test Bear",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
    )
    val blueCreature = CardDefinition.creature(
        name = "Blue Test Bear",
        manaCost = ManaCost.parse("{U}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
    )
    val colorlessCreature = CardDefinition.creature(
        name = "Colorless Test Golem",
        manaCost = ManaCost.parse("{2}"),
        subtypes = setOf(Subtype("Golem")),
        power = 2,
        toughness = 2,
    )

    val colorAbilityId = PitOfOfferings.activatedAbilities[1].id

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(PitOfOfferings, whiteCreature, blueCreature, colorlessCreature))
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    /** Put the pit on the battlefield (untapped) with [names] exiled and linked to it. */
    fun GameTestDriver.pitWithLinkedExile(owner: EntityId, names: List<String>): Pair<EntityId, List<EntityId>> {
        val pit = putPermanentOnBattlefield(owner, "Pit of Offerings")
        val exiled = names.map { putCardInExile(owner, it) }
        replaceState(state.updateEntity(pit) { it.with(LinkedExileComponent(exiled)) })
        return pit to exiled
    }

    test("color ability offers exactly the exiled cards' colors (pauses to choose)") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val (pit, _) = d.pitWithLinkedExile(you, listOf("White Test Bear", "Blue Test Bear"))

        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe true
        val decision = d.pendingDecision
        decision.shouldBeInstanceOf<ChooseColorDecision>()
        decision.availableColors.toSet() shouldBe setOf(Color.WHITE, Color.BLUE)

        d.submitDecision(you, ColorChosenResponse(decision.id, Color.BLUE))
        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()!!
        pool.getAmount(Color.BLUE) shouldBe 1
        pool.getAmount(Color.WHITE) shouldBe 0
    }

    test("single exiled color needs no choice") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val (pit, _) = d.pitWithLinkedExile(you, listOf("White Test Bear"))

        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe false
        d.state.getEntity(you)?.get<ManaPoolComponent>()!!.getAmount(Color.WHITE) shouldBe 1
    }

    test("colorless-only exiled cards produce no mana") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val (pit, _) = d.pitWithLinkedExile(you, listOf("Colorless Test Golem"))

        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe false
        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()
        Color.entries.sumOf { pool?.getAmount(it) ?: 0 } shouldBe 0
    }

    test("no exiled cards produce no mana") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val pit = d.putPermanentOnBattlefield(you, "Pit of Offerings")

        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe false
        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()
        Color.entries.sumOf { pool?.getAmount(it) ?: 0 } shouldBe 0
    }

    test("a card that has left exile no longer contributes its color") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val (pit, exiled) = d.pitWithLinkedExile(you, listOf("White Test Bear", "Blue Test Bear"))
        // The blue card leaves exile (e.g., cast/returned): still in LinkedExileComponent ids,
        // but no longer in the exile zone → drops out of the color pool.
        d.moveToGraveyard(exiled[1])

        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe false // only white remains → auto-picked
        d.state.getEntity(you)?.get<ManaPoolComponent>()!!.getAmount(Color.WHITE) shouldBe 1
    }

    test("ETB exiles up to three cards across graveyards, links them, and enters tapped") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.state.turnOrder.first { it != you }
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val myCard = d.putCardInGraveyard(you, "White Test Bear")
        val oppCard = d.putCardInGraveyard(opponent, "Blue Test Bear")

        val pit = d.putCardInHand(you, "Pit of Offerings")
        d.playLand(you, pit)

        // The ETB trigger targets up to three cards from graveyards.
        d.pendingDecision.shouldNotBeNull()
        d.submitTargetSelection(you, listOf(myCard, oppCard))
        while (d.stackSize > 0) d.bothPass()

        // "This land enters tapped."
        d.isTapped(pit) shouldBe true
        // Both cards exiled and linked to the land.
        d.getExile(you) shouldContainExactlyInAnyOrder listOf(myCard)
        d.getExile(opponent) shouldContainExactlyInAnyOrder listOf(oppCard)
        d.state.getEntity(pit)?.get<LinkedExileComponent>()?.exiledIds?.toSet() shouldBe
            setOf(myCard, oppCard)

        // The color ability now offers both exiled colors.
        d.untapPermanent(pit)
        val result = d.submit(ActivateAbility(playerId = you, sourceId = pit, abilityId = colorAbilityId))
        result.isPaused shouldBe true
        val decision = d.pendingDecision as ChooseColorDecision
        decision.availableColors.toSet() shouldBe setOf(Color.WHITE, Color.BLUE)
        d.submitDecision(you, ColorChosenResponse(decision.id, Color.WHITE))
        d.state.getEntity(you)?.get<ManaPoolComponent>()!!.getAmount(Color.WHITE) shouldBe 1
    }
})
