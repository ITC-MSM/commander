package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.MolderingGymWeightRoom
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Moldering Gym // Weight Room (DSK 190) — split-layout Room (CR 709.5).
 *
 * Moldering Gym {2}{G} — "When you unlock this door, search your library for a basic land card,
 *                         put it onto the battlefield tapped, then shuffle."
 * Weight Room {5}{G}   — "When you unlock this door, manifest dread, then put three +1/+1 counters
 *                         on that creature."
 */
class MolderingGymWeightRoomTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(MolderingGymWeightRoom)
        d.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("casting Moldering Gym fetches a basic land onto the battlefield tapped") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val landsBefore = d.getLands(p1).size

        val roomId = d.putCardInHand(p1, MolderingGymWeightRoom.name)
        d.giveMana(p1, Color.GREEN, 1)
        d.giveColorlessMana(p1, 2)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))

        // Resolve the Room, then its unlock trigger, until the library search pauses.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Moldering Gym"))

        // Search pauses to pick a basic land from the library.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        val forest = pick.options.first()
        d.submitDecision(p1, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(forest)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The fetched land is on the battlefield, tapped.
        d.getLands(p1).size shouldBe landsBefore + 1
        d.getLands(p1) shouldContain forest
        d.isTapped(forest) shouldBe true
    }

    test("casting Weight Room manifests dread and puts three +1/+1 counters on that creature") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top two of library for manifest dread.
        d.putCardOnTopOfLibrary(p1, "Forest")
        val creature = d.putCardOnTopOfLibrary(p1, "Grizzly Bears")

        val roomId = d.putCardInHand(p1, MolderingGymWeightRoom.name)
        d.giveMana(p1, Color.GREEN, 1)
        d.giveColorlessMana(p1, 5)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 1))

        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Weight Room"))

        // Manifest dread pauses to choose which card to manifest.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(p1, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(creature)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The manifested creature is a face-down 2/2 with three +1/+1 counters → 5/5.
        d.state.getEntity(creature)?.get<FaceDownComponent>() shouldBe FaceDownComponent
        d.state.getEntity(creature)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
        StateProjector().getProjectedPower(d.state, creature) shouldBe 5
        StateProjector().getProjectedToughness(d.state, creature) shouldBe 5
    }
})
