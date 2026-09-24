package com.wingedsheep.engine.multiplayer

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutorRegistry
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.mtg.sets.definitions.blb.cards.AgateBladeAssassin
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Agate-Blade Assassin (BLB): "Whenever this creature attacks, DEFENDING PLAYER loses
 * 1 life and you gain 1 life."
 *
 * Regression guard: the life loss used to be Player.EachOpponent, which in multiplayer
 * drained every opponent instead of only the player being attacked (CR 802.2a).
 */
class AgateBladeAssassinMultiplayerTest : FunSpec({

    fun initFourPlayerGame(registry: CardRegistry): Pair<GameState, List<EntityId>> {
        val deck = Deck(cards = List(40) { "Agate-Blade Assassin" })
        val result = GameInitializer(registry).initializeGame(
            GameConfig(
                players = (1..4).map { PlayerConfig("Player $it", deck, 20) },
                skipMulligans = true,
                startingPlayerIndex = 0
            )
        )
        return result.state to result.playerIds
    }

    test("attack trigger drains only the defending player, not every opponent") {
        val registry = CardRegistry()
        registry.register(AgateBladeAssassin)
        val (initial, players) = initFourPlayerGame(registry)

        // Materialize the assassin for player 0, declared attacking player 2.
        val assassinId = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = AgateBladeAssassin.name,
                name = AgateBladeAssassin.name,
                manaCost = AgateBladeAssassin.manaCost,
                typeLine = AgateBladeAssassin.typeLine,
                ownerId = players[0]
            ),
            ControllerComponent(players[0]),
            AttackingComponent(defenderId = players[2])
        )
        val state = initial
            .withEntity(assassinId, container)
            .addToZone(ZoneKey(players[0], Zone.BATTLEFIELD), assassinId)

        // Execute the card's actual attack-trigger effect.
        val triggerEffect = AgateBladeAssassin.triggeredAbilities.first().effect
        val context = EffectContext(sourceId = assassinId, controllerId = players[0])
        val result = EffectExecutorRegistry(ZoneTransitionService(registry), cardRegistry = registry).execute(state, triggerEffect, context)

        fun life(s: GameState, p: EntityId) = s.getEntity(p)?.get<LifeTotalComponent>()?.life

        life(result.state, players[0]) shouldBe 21 // "you gain 1 life"
        life(result.state, players[1]) shouldBe 20 // not attacked — untouched
        life(result.state, players[2]) shouldBe 19 // defending player loses 1
        life(result.state, players[3]) shouldBe 20 // not attacked — untouched
    }
})
