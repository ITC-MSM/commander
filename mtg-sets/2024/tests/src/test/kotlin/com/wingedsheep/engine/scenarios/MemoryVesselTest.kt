package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.state.components.player.PlayerCantPlayFromHandComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.MemoryVessel
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Memory Vessel {3}{R}{R} — Artifact.
 * "{T}, Exile this artifact: Each player exiles the top seven cards of their library. Until your
 *  next turn, players may play cards they exiled this way, and they can't play cards from their
 *  hand. Activate only as a sorcery."
 *
 * Exercises the load-bearing parts:
 *  - per-player exile of the top seven (`ForEachPlayer` + per-player Gather/Move),
 *  - each player gets a may-play permission for *their own* exiled cards,
 *  - each player is barred from playing cards from their hand (CantPlayCardsFromHand),
 *  - the restriction expires on the *activating* player's next turn — not each player's own.
 */
class MemoryVesselTest : FunSpec({

    val abilityId = MemoryVessel.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MemoryVessel))
        return driver
    }

    fun GameTestDriver.libraryOf(playerId: com.wingedsheep.sdk.model.EntityId) =
        state.getZone(ZoneKey(playerId, Zone.LIBRARY))

    test("Each player exiles the top seven of their own library; hands are locked, exiles are playable") {
        val driver = createDriver()
        // Big libraries so seven can be exiled per player without decking.
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Forest" to 30))
        val alice = driver.activePlayer!!
        val bob = driver.getOpponent(alice)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack a known, castable card on top of each library so we can assert it lands in
        // exile and becomes a may-play action. (Grizzly Bears is a vanilla 2/2.)
        val aliceTop = driver.putCardOnTopOfLibrary(alice, "Grizzly Bears")
        val bobTop = driver.putCardOnTopOfLibrary(bob, "Grizzly Bears")

        // Each player holds a creature in hand — these must become UN-castable.
        val aliceHandCreature = driver.putCardInHand(alice, "Centaur Courser")
        val bobHandCreature = driver.putCardInHand(bob, "Centaur Courser")

        // Memory Vessel in play, untapped, no summoning sickness (artifact, but be safe).
        val vessel = driver.putPermanentOnBattlefield(alice, "Memory Vessel")
        driver.replaceState(
            driver.state.updateEntity(vessel) { it.without<SummoningSicknessComponent>() }
        )

        val aliceLibBefore = driver.libraryOf(alice).size
        val bobLibBefore = driver.libraryOf(bob).size

        driver.submitSuccess(
            ActivateAbility(
                playerId = alice,
                sourceId = vessel,
                abilityId = abilityId,
                targets = emptyList(),
                costPayment = null,
            )
        )
        // Resolve the ability off the stack.
        driver.bothPass()

        // Seven cards left each library.
        driver.libraryOf(alice).size shouldBe (aliceLibBefore - 7)
        driver.libraryOf(bob).size shouldBe (bobLibBefore - 7)

        // The stacked top card of each library is now in that player's exile.
        driver.getExile(alice) shouldContain aliceTop
        driver.getExile(bob) shouldContain bobTop

        // Memory Vessel exiled itself as part of the cost.
        driver.findPermanent(alice, "Memory Vessel") shouldBe null

        // Both players carry the "can't play cards from hand" restriction.
        driver.state.getEntity(alice)?.has<PlayerCantPlayFromHandComponent>() shouldBe true
        driver.state.getEntity(bob)?.has<PlayerCantPlayFromHandComponent>() shouldBe true

        // Alice can play her exiled card but NOT the creature in her hand.
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val aliceActions = enumerator.enumerate(driver.state, alice, EnumerationMode.FULL)
        aliceActions.any { it.actionType == "CastSpell" && cardIdOf(it) == aliceTop } shouldBe true
        aliceActions.any { it.actionType == "CastSpell" && cardIdOf(it) == aliceHandCreature } shouldBe false

        // Land plays from Alice's HAND are barred (the restriction covers lands). Lands granted a
        // may-play permission from exile are still playable, so check the hand lands specifically.
        val aliceHandLands = driver.getHand(alice)
        aliceActions.none { it.actionType == "PlayLand" && cardIdOf(it) in aliceHandLands } shouldBe true
    }

    test("The can't-play-from-hand restriction expires on the ACTIVATING player's next turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Forest" to 30))
        val alice = driver.activePlayer!!
        val bob = driver.getOpponent(alice)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vessel = driver.putPermanentOnBattlefield(alice, "Memory Vessel")
        driver.replaceState(
            driver.state.updateEntity(vessel) { it.without<SummoningSicknessComponent>() }
        )

        driver.submitSuccess(
            ActivateAbility(
                playerId = alice,
                sourceId = vessel,
                abilityId = abilityId,
                targets = emptyList(),
                costPayment = null,
            )
        )
        driver.bothPass()

        driver.state.getEntity(alice)?.has<PlayerCantPlayFromHandComponent>() shouldBe true
        driver.state.getEntity(bob)?.has<PlayerCantPlayFromHandComponent>() shouldBe true

        // Advance into Bob's turn. The window keys off Alice's next turn, so BOTH restrictions
        // must persist through Bob's whole turn (an owner-keyed lifetime would wrongly lift
        // Bob's at the start of his own untap).
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 500)
        driver.activePlayer shouldBe bob
        driver.state.getEntity(alice)?.has<PlayerCantPlayFromHandComponent>() shouldBe true
        driver.state.getEntity(bob)?.has<PlayerCantPlayFromHandComponent>() shouldBe true

        // Advance to Alice's next turn — both restrictions lift at her untap.
        driver.passPriorityUntil(Step.END, maxPasses = 500)
        driver.bothPass()
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 500)
        driver.activePlayer shouldBe alice
        driver.state.getEntity(alice)?.has<PlayerCantPlayFromHandComponent>() shouldBe false
        driver.state.getEntity(bob)?.has<PlayerCantPlayFromHandComponent>() shouldBe false
    }
})

private fun cardIdOf(action: com.wingedsheep.engine.legalactions.LegalAction): com.wingedsheep.sdk.model.EntityId? =
    when (val a = action.action) {
        is com.wingedsheep.engine.core.CastSpell -> a.cardId
        is com.wingedsheep.engine.core.PlayLand -> a.cardId
        else -> null
    }
