package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.InkfathomDivers
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SummonTheSchool
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Summon the School (LRW #42) — {3}{W} Kindred Sorcery — Merfolk.
 *
 * "Create two 1/1 blue Merfolk Wizard creature tokens.
 *  Tap four untapped Merfolk you control: Return this card from your graveyard to your hand."
 *
 * The recursion is the part worth pinning: it is an activated ability of a card in the *graveyard*
 * whose only cost is a tap-four-permanents cost. That combination — `activateFromZone = GRAVEYARD`
 * with a non-mana selection cost — has no other card in the corpus, so both halves are checked
 * here: the enumerator must surface the ability while the card sits in the graveyard, and the
 * payment must actually demand four *untapped* Merfolk before the card comes back.
 */
class SummonTheSchoolScenarioTest : FunSpec({

    val abilityId = SummonTheSchool.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonTheSchool, InkfathomDivers))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, source: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        return enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
            .any { (it.action as? ActivateAbility)?.sourceId == source }
    }

    fun activate(driver: GameTestDriver, player: EntityId, source: EntityId, tapping: List<EntityId>) =
        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = source,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = tapping),
            )
        )

    test("the spell makes two 1/1 blue Merfolk Wizard tokens") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val spell = driver.putCardInHand(me, "Summon the School")
        driver.giveMana(me, Color.WHITE, 4)
        driver.castSpell(me, spell).isSuccess shouldBe true
        driver.bothPass()

        val tokens = driver.getCreatures(me)
        tokens.size shouldBe 2
        tokens.forEach { token ->
            driver.state.projectedState.getPower(token) shouldBe 1
            driver.state.projectedState.getToughness(token) shouldBe 1
            driver.getCardName(token) shouldBe "Merfolk Wizard Token"
        }

        // The sorcery itself is now in the graveyard, where its recursion lives.
        driver.getGraveyardCardNames(me) shouldBe listOf("Summon the School")
    }

    test("the recursion is offered while the card sits in the graveyard") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val card = driver.putCardInGraveyard(me, "Summon the School")
        repeat(4) { driver.putCreatureOnBattlefield(me, "Inkfathom Divers") }

        canActivate(driver, me, card) shouldBe true
    }

    test("three Merfolk can't pay a cost that asks for four") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val card = driver.putCardInGraveyard(me, "Summon the School")
        val divers = List(3) { driver.putCreatureOnBattlefield(me, "Inkfathom Divers") }

        activate(driver, me, card, divers).isSuccess shouldBe false
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(card) shouldBe true
    }

    test("an already-tapped Merfolk doesn't count toward the four") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val card = driver.putCardInGraveyard(me, "Summon the School")
        val divers = List(4) { driver.putCreatureOnBattlefield(me, "Inkfathom Divers") }
        driver.tapPermanent(divers.first())

        activate(driver, me, card, divers).isSuccess shouldBe false
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(card) shouldBe true
    }

    test("four untapped Merfolk return the card from the graveyard to hand, and are tapped for it") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val card = driver.putCardInGraveyard(me, "Summon the School")
        val divers = List(4) { driver.putCreatureOnBattlefield(me, "Inkfathom Divers") }

        activate(driver, me, card, divers).isSuccess shouldBe true
        var guard = 0
        while (driver.isPaused && guard++ < 20) driver.autoResolveDecision()
        driver.bothPass()

        divers.forEach { driver.isTapped(it) shouldBe true }
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(card) shouldBe false
        driver.state.getZone(ZoneKey(me, Zone.HAND)).contains(card) shouldBe true
    }
})
