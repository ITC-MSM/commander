package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SunkenCitadel
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Sunken Citadel — Land — Cave.
 *   "This land enters tapped. As it enters, choose a color.
 *    {T}: Add one mana of the chosen color.
 *    {T}: Add two mana of the chosen color. Spend this mana only to activate abilities of land sources."
 *
 * Covers: (a) enters-tapped + choose-a-color ETB replacement (the color is stored on the
 * permanent), (b) {T}: add one mana of the chosen color, and (c) {T}: add two mana of the
 * chosen color carrying the land-source spend restriction
 * ([ManaRestriction.CardTypeSpellsOrAbilitiesOnly] keyed to [CardType.LAND], abilities only).
 */
class SunkenCitadelScenarioTest : FunSpec({

    val oneManaAbilityId = SunkenCitadel.activatedAbilities[0].id
    val twoManaAbilityId = SunkenCitadel.activatedAbilities[1].id

    val landAbilityRestriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
        cardType = CardType.LAND,
        allowSpells = false,
        allowAbilities = true,
    )

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(SunkenCitadel))
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("enters tapped and pauses to choose a color as it enters") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val citadel = d.putCardInHand(you, "Sunken Citadel")
        d.playLand(you, citadel)

        // "As it enters, choose a color."
        d.isPaused shouldBe true
        val decision = d.pendingDecision
        decision.shouldBeInstanceOf<ChooseColorDecision>()
        d.submitDecision(you, ColorChosenResponse(decision.id, Color.BLUE))

        // "This land enters tapped."
        d.isTapped(citadel) shouldBe true
    }

    test("{T}: adds one mana of the chosen color") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val citadel = d.putCardInHand(you, "Sunken Citadel")
        d.playLand(you, citadel)
        val decision = d.pendingDecision as ChooseColorDecision
        d.submitDecision(you, ColorChosenResponse(decision.id, Color.BLUE))

        // Untap so the {T} cost can be paid this turn.
        d.untapPermanent(citadel)

        val result = d.submit(ActivateAbility(playerId = you, sourceId = citadel, abilityId = oneManaAbilityId))
        // No further color choice — the color was fixed on entry.
        result.isPaused shouldBe false

        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()!!
        pool.getAmount(Color.BLUE) shouldBe 1
        pool.getAmount(Color.RED) shouldBe 0
        pool.restrictedMana.size shouldBe 0
    }

    test("{T}: adds two mana of the chosen color restricted to activating land abilities") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val citadel = d.putCardInHand(you, "Sunken Citadel")
        d.playLand(you, citadel)
        val decision = d.pendingDecision as ChooseColorDecision
        d.submitDecision(you, ColorChosenResponse(decision.id, Color.BLUE))

        d.untapPermanent(citadel)

        val result = d.submit(ActivateAbility(playerId = you, sourceId = citadel, abilityId = twoManaAbilityId))
        result.isPaused shouldBe false

        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()!!
        // Two mana of the chosen color, each carrying the land-source restriction.
        pool.getAmount(Color.BLUE) shouldBe 0
        pool.restrictedMana.size shouldBe 2
        pool.restrictedMana.all { it.color == Color.BLUE } shouldBe true
        pool.restrictedMana.all { it.restriction == landAbilityRestriction } shouldBe true
    }
})
