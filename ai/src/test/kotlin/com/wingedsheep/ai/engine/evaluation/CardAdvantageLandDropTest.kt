package com.wingedsheep.ai.engine.evaluation

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.player.LandDropsComponent
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe

/**
 * `AiProfile.landDropIsNotCardLoss`: a land drop relocates a card, it does not spend one.
 *
 * Asserted on [CardAdvantage] alone rather than through a whole evaluator, because the claim is
 * about this one feature and the composite would let [Tempo] and `BoardPresence` cover a wrong
 * number here with a right one there — which is exactly the confusion the flag exists to undo.
 * That the fix moves the *decision* is `PuzzleSuiteTest`'s `sequencing-02`.
 */
class CardAdvantageLandDropTest : FunSpec({

    val registry = CardRegistry().apply { register(TestCards.all) }
    val penalty = -2.0 // what `concave-hand-2`, and therefore the live profile, charges

    fun boot(): GameState = GameInitializer(registry).initializeGame(
        GameConfig(
            players = (1..2).map { PlayerConfig("P$it", Deck.of("Forest" to 30, "Grizzly Bears" to 10)) },
            skipMulligans = true,
            startingPlayerIndex = 0,
            seed = 424242L,
        )
    ).state

    /** Empty both hands, so a test's own [draw]s are the whole hand. */
    fun GameState.withEmptyHands(): GameState = turnOrder.fold(this) { state, playerId ->
        state.getZone(playerId, Zone.HAND).fold(state) { s, cardId ->
            s.removeFromZone(ZoneKey(playerId, Zone.HAND), cardId)
                .addToZone(ZoneKey(playerId, Zone.LIBRARY), cardId)
        }
    }

    /** Move one [cardName] from [playerId]'s library into their hand. */
    fun GameState.draw(playerId: EntityId, cardName: String): GameState {
        val cardId = getZone(playerId, Zone.LIBRARY)
            .first { getEntity(it)?.get<CardComponent>()?.name == cardName }
        return removeFromZone(ZoneKey(playerId, Zone.LIBRARY), cardId)
            .addToZone(ZoneKey(playerId, Zone.HAND), cardId)
    }

    /** Play [playerId]'s first land from hand, spending the land drop — what `PlayLandHandler` does. */
    fun GameState.playLand(playerId: EntityId): GameState {
        val cardId = getZone(playerId, Zone.HAND)
            .first { getEntity(it)?.get<CardComponent>()?.isLand == true }
        return removeFromZone(ZoneKey(playerId, Zone.HAND), cardId)
            .addToZone(ZoneKey(playerId, Zone.BATTLEFIELD), cardId)
            .updateEntity(cardId) { it.with(ControllerComponent(playerId)) }
            .updateEntity(playerId) { it.with(it.get<LandDropsComponent>()!!.use()) }
    }

    fun GameState.cards(playerId: EntityId, landDropIsNotCardLoss: Boolean): Double =
        CardAdvantage.score(this, projectedState, playerId, penalty, landDropIsNotCardLoss)

    test("off, a land drop is charged as card loss — the historical behaviour") {
        val before = boot().withEmptyHands().let { it.draw(it.turnOrder[0], "Forest") }
        val me = before.turnOrder[0]
        val after = before.playLand(me)

        withClue("hand 1 -> 0 costs the whole first-card marginal, which is the cliff") {
            before.cards(me, landDropIsNotCardLoss = false) shouldBe 3.0
            after.cards(me, landDropIsNotCardLoss = false) shouldBe 0.0
        }
    }

    test("on, a land drop is exactly card-neutral") {
        val before = boot().withEmptyHands().let { it.draw(it.turnOrder[0], "Forest") }
        val me = before.turnOrder[0]
        val after = before.playLand(me)

        after.cards(me, landDropIsNotCardLoss = true) shouldBe before.cards(me, landDropIsNotCardLoss = true)
    }

    test("on, only one land is held back — the second is a card again") {
        // Two lands in hand and one drop: the drop earmarks one of them, so the *other* is worth a
        // card. Without the cap this would read a hand of nothing but lands as topdeck mode.
        val base = boot().withEmptyHands()
        val me = base.turnOrder[0]
        val oneLand = base.draw(me, "Forest")
        val twoLands = oneLand.draw(me, "Forest")

        twoLands.cards(me, landDropIsNotCardLoss = true) shouldBeGreaterThan
            oneLand.cards(me, landDropIsNotCardLoss = true)

        withClue("after the drop is spent, the land left in hand counts like any other card") {
            val played = twoLands.playLand(me)
            played.cards(me, landDropIsNotCardLoss = true) shouldBe
                oneLand.cards(me, landDropIsNotCardLoss = false)
        }
    }

    test("on, a spell is still a card — casting one still costs card advantage") {
        val base = boot().withEmptyHands()
        val me = base.turnOrder[0]
        val withSpell = base.draw(me, "Grizzly Bears")

        withClue("the earmark is for lands only; a 2/2 in hand is not mana") {
            withSpell.cards(me, landDropIsNotCardLoss = true) shouldBe
                withSpell.cards(me, landDropIsNotCardLoss = false)
        }
        withClue("and spending it still steps off the cliff") {
            base.cards(me, landDropIsNotCardLoss = true) shouldBeLessThan
                withSpell.cards(me, landDropIsNotCardLoss = true)
        }
    }
})
