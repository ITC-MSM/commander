package com.wingedsheep.engine.multiplayer

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * CR 805.4d: the shared upkeep of two opposing teammates yields one trigger per
 * opponent when the ability's effect refers to that individual opponent.
 */
class TwoHeadedGiantStepTriggerMultiplicityTest : FunSpec({

    fun moveToCommandZone(state: com.wingedsheep.engine.state.GameState, ownerId: com.wingedsheep.sdk.model.EntityId, cardName: String): Pair<com.wingedsheep.engine.state.GameState, com.wingedsheep.sdk.model.EntityId> {
        val source = listOf(Zone.LIBRARY, Zone.HAND).firstNotNullOfOrNull { zone ->
            state.getZone(ZoneKey(ownerId, zone)).firstOrNull {
                state.getEntity(it)?.get<CardComponent>()?.name == cardName
            }?.let { zone to it }
        } ?: error("$cardName was not found in $ownerId's library or hand")
        return state
            .removeFromZone(ZoneKey(ownerId, source.first), source.second)
            .addToZone(ZoneKey(ownerId, Zone.COMMAND), source.second) to source.second
    }

    val opponentUpkeepWitness = card("Opponent Upkeep Witness") {
        manaCost = "{1}{B}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EachOpponentUpkeep
            triggerZones = setOf(Zone.COMMAND)
            effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        }
    }

    val unboundOpponentUpkeepWitness = card("Unbound Opponent Upkeep Witness") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EachOpponentUpkeep
            triggerZones = setOf(Zone.COMMAND)
            effect = Effects.DrawCards(1)
        }
    }

    val conditionalOpponentUpkeepWitness = card("Conditional Opponent Upkeep Witness") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EachOpponentUpkeep
            triggerZones = setOf(Zone.COMMAND)
            triggerCondition = Conditions.TriggeringPlayerIs(Player.TriggeringPlayer)
            effect = Effects.DrawCards(1)
        }
    }

    test("an individual-opponent upkeep trigger fires once for each active opposing teammate") {
        val registry = CardRegistry().also { it.register(TestCards.all + opponentUpkeepWitness) }
        val initialized = GameInitializer(registry).initializeGame(
            GameConfig(
                format = Format.TwoHeadedGiant(),
                players = (0..3).map { index ->
                    PlayerConfig(
                        "Player $index",
                        if (index == 0) {
                            Deck.of("Opponent Upkeep Witness" to 1, "Forest" to 39)
                        } else {
                            Deck.of("Forest" to 40)
                        }
                    )
                },
                teams = listOf(listOf(0, 1), listOf(2, 3)),
                startingPlayerIndex = 2,
                skipMulligans = true,
            )
        )
        val p = initialized.playerIds
        val (state, witness) = moveToCommandZone(initialized.state, p[0], "Opponent Upkeep Witness")

        val triggers = TriggerDetector(registry).detectPhaseStepTriggers(state, Step.UPKEEP, p[2])
            .filter { it.sourceId == witness }

        triggers shouldHaveSize 2
        triggers.map { it.triggerContext.triggeringPlayerId ?: it.triggerContext.triggeringEntityId }
            .shouldContainExactlyInAnyOrder(p[2], p[3])
    }

    test("an unbound each-opponent upkeep trigger fires once for the shared opponent upkeep") {
        val registry = CardRegistry().also { it.register(TestCards.all + unboundOpponentUpkeepWitness) }
        val initialized = GameInitializer(registry).initializeGame(
            GameConfig(
                format = Format.TwoHeadedGiant(),
                players = (0..3).map { index ->
                    PlayerConfig(
                        "Player $index",
                        if (index == 0) Deck.of("Unbound Opponent Upkeep Witness" to 1, "Forest" to 39)
                        else Deck.of("Forest" to 40)
                    )
                },
                teams = listOf(listOf(0, 1), listOf(2, 3)),
                startingPlayerIndex = 2,
                skipMulligans = true,
            )
        )
        val p = initialized.playerIds
        val (state, witness) = moveToCommandZone(initialized.state, p[0], "Unbound Opponent Upkeep Witness")

        val triggers = TriggerDetector(registry).detectPhaseStepTriggers(state, Step.UPKEEP, p[2])
            .filter { it.sourceId == witness }

        triggers shouldHaveSize 1
        triggers.single().triggerContext.triggeringPlayerId shouldBe p[2]
    }

    test("an intervening-if reference to the individual opponent also creates one trigger per teammate") {
        val registry = CardRegistry().also { it.register(TestCards.all + conditionalOpponentUpkeepWitness) }
        val initialized = GameInitializer(registry).initializeGame(
            GameConfig(
                format = Format.TwoHeadedGiant(),
                players = (0..3).map { index ->
                    PlayerConfig(
                        "Player $index",
                        if (index == 0) Deck.of("Conditional Opponent Upkeep Witness" to 1, "Forest" to 39)
                        else Deck.of("Forest" to 40)
                    )
                },
                teams = listOf(listOf(0, 1), listOf(2, 3)),
                startingPlayerIndex = 2,
                skipMulligans = true,
            )
        )
        val p = initialized.playerIds
        val (state, witness) = moveToCommandZone(initialized.state, p[0], "Conditional Opponent Upkeep Witness")

        val triggers = TriggerDetector(registry).detectPhaseStepTriggers(state, Step.UPKEEP, p[2])
            .filter { it.sourceId == witness }

        triggers shouldHaveSize 2
        triggers.map { it.triggerContext.triggeringPlayerId }.shouldContainExactlyInAnyOrder(p[2], p[3])
    }
})
