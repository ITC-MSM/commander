package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.RiseOfTheWitchKing
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rise of the Witch-king (LTR).
 *
 * Symmetric "each player sacrifices a creature of their choice" — both `ForceSacrifice`
 * legs capture a `EntitySnapshot` and inject it into the underlying `EffectContinuation`
 * (the snapshot-threading hook), so the `YouSacrificedThisWay` rider can gate on whether
 * the cast's controller actually sacrificed. The rider's reanimation is *not* a target —
 * the oracle has no "target" word — so the player picks a permanent card from their
 * graveyard at resolution via a `SelectFromCollectionEffect`.
 */
class RiseOfTheWitchKingTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RiseOfTheWitchKing))
        return driver
    }

    test("rider lets you choose a permanent from your graveyard at resolution; no targeting at cast") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opp = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val yourBear = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val oppBear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // Pre-seed a permanent card in your graveyard for the rider to reanimate.
        val reanimatable = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val gv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = reanimatable,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(gv.state)

        val rise = driver.putCardInHand(active, "Rise of the Witch-king")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveMana(active, Color.GREEN, 1)
        driver.giveColorlessMana(active, 2)
        // No target at cast time — the reanimation is resolution-time.
        driver.castSpell(active, rise).isSuccess shouldBe true
        driver.bothPass()

        // After auto-sacrifice on both sides, the rider's SelectFromCollection pauses.
        val pending = driver.pendingDecision
        check(pending is SelectCardsDecision) {
            "expected a SelectCardsDecision for the reanimation rider's selection, got $pending"
        }
        driver.submitDecision(active, CardsSelectedResponse(pending.id, listOf(reanimatable)))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Both bears went to their owners' graveyards.
        driver.state.getGraveyard(active).contains(yourBear) shouldBe true
        driver.state.getGraveyard(opp).contains(oppBear) shouldBe true
        // The pre-seeded reanimatable is now on the battlefield.
        driver.state.getBattlefield().contains(reanimatable) shouldBe true
    }

    test("rider allows choosing zero — graveyard reanimation is optional ('may')") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opp = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // Stage a graveyard card so the SelectFromCollection prompt opens.
        val parked = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val gv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = parked,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(gv.state)

        val rise = driver.putCardInHand(active, "Rise of the Witch-king")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveMana(active, Color.GREEN, 1)
        driver.giveColorlessMana(active, 2)
        driver.castSpell(active, rise).isSuccess shouldBe true
        driver.bothPass()

        val pending = driver.pendingDecision
        check(pending is SelectCardsDecision) {
            "expected a SelectCardsDecision for the optional rider, got $pending"
        }
        // Choose zero — the spell still resolves, parked stays in the graveyard.
        driver.submitDecision(active, CardsSelectedResponse(pending.id, emptyList()))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.state.getGraveyard(active).contains(parked) shouldBe true
        driver.state.getBattlefield().contains(parked) shouldBe false
    }

    test("the creature you sacrificed is NOT offered for reanimation ('another permanent card')") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opp = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The only creature you control — this is what you'll sacrifice to Rise.
        val sacrificed = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // A DIFFERENT permanent already in your graveyard, so the SelectFromCollection
        // prompt still opens — proving the pool is non-empty yet excludes the sacrificed card.
        val other = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val gv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = other,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(gv.state)

        val rise = driver.putCardInHand(active, "Rise of the Witch-king")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveMana(active, Color.GREEN, 1)
        driver.giveColorlessMana(active, 2)
        driver.castSpell(active, rise).isSuccess shouldBe true
        driver.bothPass()

        val pending = driver.pendingDecision
        check(pending is SelectCardsDecision) {
            "expected a SelectCardsDecision for the reanimation rider, got $pending"
        }
        // The sacrificed creature is in your graveyard now, but must NOT be a candidate;
        // the pre-seeded `other` card is.
        pending.options.contains(sacrificed) shouldBe false
        pending.options.contains(other) shouldBe true

        driver.submitDecision(active, CardsSelectedResponse(pending.id, emptyList()))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // The sacrificed creature stays dead — it could never have been returned.
        driver.state.getGraveyard(active).contains(sacrificed) shouldBe true
        driver.state.getBattlefield().contains(sacrificed) shouldBe false
    }

    test("rider does NOT fire when you sacrificed nothing (you control no creature)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opp = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // You control NO creature, so you sacrifice nothing → YouSacrificedThisWay is false.
        // The opponent has a creature to sacrifice so the edict half still does something.
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // Pre-seed a permanent in your graveyard: if the rider wrongly fired, it would be
        // offered for reanimation. It must stay put and no decision must open.
        val parked = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val gv = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = parked,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(gv.state)

        val rise = driver.putCardInHand(active, "Rise of the Witch-king")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveMana(active, Color.GREEN, 1)
        driver.giveColorlessMana(active, 2)
        driver.castSpell(active, rise).isSuccess shouldBe true
        driver.bothPass()

        // The intervening condition is false, so the rider never opens a selection.
        driver.pendingDecision shouldBe null
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.state.getGraveyard(active).contains(parked) shouldBe true
        driver.state.getBattlefield().contains(parked) shouldBe false
    }
})
