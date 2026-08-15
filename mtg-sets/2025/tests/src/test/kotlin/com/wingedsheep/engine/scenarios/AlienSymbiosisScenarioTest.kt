package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Alien Symbiosis (SPM #50) — {1}{B} Enchantment — Aura.
 *
 * "Enchant creature
 *  Enchanted creature gets +1/+1, has menace, and is a Symbiote in addition to its other types.
 *  You may cast this card from your graveyard by discarding a card in addition to paying its
 *  other costs."
 *
 * Exercises:
 *  - the Aura's continuous +1/+1 / menace / Symbiote-subtype grant while attached; and
 *  - the self cast-from-graveyard permission bundled with a discard-a-card additional cost
 *    (`MayCastSelfFromZones(GRAVEYARD, additionalCost = Costs.additional.DiscardCards(1))`).
 */
class AlienSymbiosisScenarioTest : FunSpec({

    val projector = StateProjector()

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun canCastFromGraveyard(driver: GameTestDriver, player: EntityId, cardId: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
        // affordable == false covers both "can't pay the mana cost" and "can't pay the bundled
        // additional cost" (e.g. no card left in hand to discard) — the enumerator still emits a
        // LegalAction in that case, just marked unaffordable.
        return actions.any {
            it.sourceZone == "GRAVEYARD" && it.affordable && (it.action as? CastSpell)?.cardId == cardId
        }
    }

    test("enchanted creature gets +1/+1, menace, and becomes a Symbiote in addition to its other types") {
        val d = newDriver()
        val you = d.player1

        val bear = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val aura = d.putCardInHand(you, "Alien Symbiosis")
        d.giveMana(you, Color.BLACK, 2)
        d.castSpell(you, aura, targets = listOf(bear))
        d.bothPass()

        val projected = d.state.projectedState
        projector.getProjectedPower(d.state, bear) shouldBe 3 // base 2 + 1
        projector.getProjectedToughness(d.state, bear) shouldBe 3 // base 2 + 1
        projected.hasKeyword(bear, Keyword.MENACE) shouldBe true
        projected.hasSubtype(bear, "Symbiote") shouldBe true
        // Kept its original creature type (added to, not replaced).
        projected.hasSubtype(bear, "Bear") shouldBe true
    }

    test("castable from the graveyard by discarding a card, in addition to paying its mana cost") {
        val d = newDriver()
        val you = d.player1

        val bear = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val aura = d.putCardInGraveyard(you, "Alien Symbiosis")
        val fodder = d.putCardInHand(you, "Grizzly Bears")
        d.giveMana(you, Color.BLACK, 2)

        canCastFromGraveyard(d, you, aura) shouldBe true

        val result = d.submit(
            CastSpell(
                playerId = you,
                cardId = aura,
                targets = listOf(ChosenTarget.Permanent(bear)),
                paymentStrategy = PaymentStrategy.FromPool,
                additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(fodder))
            )
        )
        result.isSuccess shouldBe true
        d.bothPass()

        // Resolved onto the battlefield attached to the bear.
        val resolvedAura = d.findPermanent(you, "Alien Symbiosis")
        (resolvedAura != null) shouldBe true
        val projected = d.state.projectedState
        projector.getProjectedPower(d.state, bear) shouldBe 3
        projected.hasKeyword(bear, Keyword.MENACE) shouldBe true
        // The discarded fodder card actually left the hand and landed in the graveyard.
        d.state.getZone(ZoneKey(you, Zone.GRAVEYARD)).contains(fodder) shouldBe true
    }

    test("casting from the graveyard fails without a card in hand to discard") {
        val d = newDriver()
        val you = d.player1

        val bear = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val aura = d.putCardInGraveyard(you, "Alien Symbiosis")
        d.giveMana(you, Color.BLACK, 2)

        // Empty the starting hand (dealt by initMirrorMatch) — no cards left to discard.
        val handZone = ZoneKey(you, Zone.HAND)
        val handCards = d.state.getZone(handZone).toList()
        d.replaceState(handCards.fold(d.state) { s, cardId -> s.removeFromZone(handZone, cardId) })
        d.state.getZone(handZone).size shouldBe 0

        canCastFromGraveyard(d, you, aura) shouldBe false

        val result = d.submit(
            CastSpell(
                playerId = you,
                cardId = aura,
                targets = listOf(ChosenTarget.Permanent(bear)),
                paymentStrategy = PaymentStrategy.FromPool,
                additionalCostPayment = AdditionalCostPayment(discardedCards = emptyList())
            )
        )
        result.isSuccess shouldBe false
    }

    test("cast normally from hand: still costs {1}{B}, no discard required") {
        val d = newDriver()
        val you = d.player1

        val bear = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val aura = d.putCardInHand(you, "Alien Symbiosis")
        d.giveMana(you, Color.BLACK, 2)

        val handSizeBefore = d.state.getZone(ZoneKey(you, Zone.HAND)).size
        d.castSpell(you, aura, targets = listOf(bear))
        d.bothPass()

        val handSizeAfter = d.state.getZone(ZoneKey(you, Zone.HAND)).size
        // Only the Aura itself left the hand (cast normally) — no additional discard.
        handSizeAfter shouldBe handSizeBefore - 1

        val projected = d.state.projectedState
        projector.getProjectedPower(d.state, bear) shouldBe 3
        projected.hasSubtype(bear, "Symbiote") shouldBe true
    }
})
