package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.DerangedAssistant
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Deranged Assistant (ISD #52) — {1}{U} 1/1 Human Wizard, "{T}, Mill a card: Add {C}."
 *
 * Covers the `CostAtom.Mill` cost vocabulary this card introduced:
 * - payment mills the top card and the ability then produces {C} (happy path),
 * - CR 701.17b — the ability is surfaced as unaffordable on an empty library and submitting it
 *   anyway is rejected, rather than silently producing free mana,
 * - the mill is a *cost*, so the card is already in the graveyard by the time the mana exists.
 *
 * **This is not a mana ability.** CR 605.1a gained "and its cost and effect don't move any card to
 * or from a library" on August 7, 2026, and the mill cost is exactly that, so the ability uses the
 * stack like any other. The split that buys — cost paid at activation, mana only at resolution — is
 * asserted below, because it is the entire observable difference and nothing on the printed card
 * says which side of it this ability falls on.
 */
class DerangedAssistantScenarioTest : FunSpec({

    val abilityId = DerangedAssistant.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DerangedAssistant)
        return driver
    }

    fun libraryOf(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId) =
        driver.state.getZone(ZoneKey(playerId, Zone.LIBRARY))

    test("activating mills the top card of your library and adds {C}") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val assistant = driver.putCreatureOnBattlefield(you, "Deranged Assistant")
        driver.removeSummoningSickness(assistant)

        val libraryBefore = libraryOf(driver, you)
        val topCard = libraryBefore.first()
        val graveyardBefore = driver.getGraveyard(you).size

        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = assistant, abilityId = abilityId))

        // CR 605.1a — the mill cost keeps this off the mana-ability path, so the ability is on the
        // stack and no mana exists yet.
        driver.assertStackSize(1)
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 0

        // Costs are still paid on activation, not on resolution: tapped, and the card already
        // milled — one card, off the top, not a chosen one.
        driver.isTapped(assistant) shouldBe true
        libraryOf(driver, you).size shouldBe libraryBefore.size - 1
        driver.getGraveyard(you).size shouldBe graveyardBefore + 1
        (topCard in driver.getGraveyard(you)) shouldBe true
        (topCard in libraryOf(driver, you)) shouldBe false

        driver.bothPass()

        // {C} produced, once the ability resolved.
        driver.assertStackSize(0)
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 1
    }

    test("CR 701.17b — the ability is unaffordable and rejected with an empty library") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val assistant = driver.putCreatureOnBattlefield(you, "Deranged Assistant")
        driver.removeSummoningSickness(assistant)

        fun assistantAction() = driver.legalActions(you)
            .single { it.action.let { a -> a is ActivateAbility && a.sourceId == assistant } }

        // With a library, the ability is offered and affordable.
        assistantAction().affordable shouldBe true

        driver.replaceState(
            driver.state.copy(zones = driver.state.zones + (ZoneKey(you, Zone.LIBRARY) to emptyList()))
        )

        // Empty library: the mill cost can't be paid, so the ability is surfaced as unaffordable…
        assistantAction().affordable shouldBe false

        // …and submitting it anyway is rejected — no free {C}.
        val result = driver.submit(ActivateAbility(playerId = you, sourceId = assistant, abilityId = abilityId))
        result.isSuccess shouldBe false
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 0
        driver.isTapped(assistant) shouldBe false
    }

    test("the mill happens as a cost, so the ability can't be activated twice off one tap") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val assistant = driver.putCreatureOnBattlefield(you, "Deranged Assistant")
        driver.removeSummoningSickness(assistant)

        val libraryBefore = libraryOf(driver, you).size
        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = assistant, abilityId = abilityId))
        val second = driver.submit(ActivateAbility(playerId = you, sourceId = assistant, abilityId = abilityId))

        second.isSuccess shouldBe false
        // Exactly one card milled — the failed second activation paid nothing.
        libraryOf(driver, you).size shouldBe libraryBefore - 1

        driver.bothPass()
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 1
    }
})
