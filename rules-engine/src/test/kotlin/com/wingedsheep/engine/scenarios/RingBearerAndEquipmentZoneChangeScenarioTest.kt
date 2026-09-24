package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.components.identity.RingBearerComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * When a permanent leaves the battlefield (e.g. exiled and returned by a blink like Meneldor,
 * Swift Savior), the returning object is a NEW object (CR 400.7) and battlefield-tied state must
 * not carry over. This exercises the Ring-bearer half of the exit-cleanup in
 * `ZoneTransitionService.moveToZone` (the path every Move effect uses): the Ring-bearer designation
 * is dropped because the returning permanent is a new object with no memory of it (CR 400.7) and the
 * designation is not a copiable value (CR 701.54b).
 *
 * The attachment-falloff half (Equipment unattaches per CR 704.5n, Auras go to the graveyard per
 * CR 704.5m) is covered by `MeneldorBilbosRingDetachScenarioTest`, which drives the full SBA pipeline
 * rather than the direct `moveToZone` blink used here.
 */
class RingBearerAndEquipmentZoneChangeScenarioTest : FunSpec({

    fun driver(vararg cards: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all +
                com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                cards.toList()
        )
        return d
    }

    fun GameTestDriver.blink(entityId: EntityId): EntityId? {
        val exiled = zones.moveToZone(state, entityId, Zone.EXILE)
        replaceState(exiled.state)
        val newId = getExile(activePlayer!!).firstOrNull() ?: return null
        val back = zones.moveToZone(state, newId, Zone.BATTLEFIELD)
        replaceState(back.state)
        return findPermanent(activePlayer!!, getCardName(newId) ?: "")
    }

    test("Ring-bearer designation does not survive a blink") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear = d.putCreatureOnBattlefield(active, "Grizzly Bears")
        d.addComponent(bear, RingBearerComponent(ownerId = active))
        d.state.getEntity(bear)?.get<RingBearerComponent>() shouldBe RingBearerComponent(ownerId = active)

        val returned = d.blink(bear)
        // The returned creature is a new object and must not be the Ring-bearer.
        (returned?.let { d.state.getEntity(it)?.get<RingBearerComponent>() }) shouldBe null
    }
})
