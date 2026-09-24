package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.core.TurnManager
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.legalactions.enumerators.*
import com.wingedsheep.engine.mechanics.SplitSecond
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Coordinator that enumerates all legal actions for a player.
 *
 * This is the engine-level equivalent of the game-server's LegalActionsCalculator.
 * It delegates to specialized ActionEnumerators for each action category, mirrors
 * the ActionProcessor/ActionHandlerRegistry pattern for enumeration.
 */
class LegalActionEnumerator(
    private val cardRegistry: CardRegistry,
    private val manaSolver: ManaSolver,
    private val costCalculator: CostCalculator,
    private val predicateEvaluator: PredicateEvaluator,
    private val conditionEvaluator: ConditionEvaluator,
    private val turnManager: TurnManager
) {
    private val combatEnumerator = CombatEnumerator()

    private val enumerators: List<ActionEnumerator> = listOf(
        PassPriorityEnumerator(),
        PlayLandEnumerator(),
        MorphCastEnumerator(),
        CastSpellEnumerator(),
        SneakCastEnumerator(),
        EmergeCastEnumerator(),
        WebSlingingCastEnumerator(),
        CyclingEnumerator(),
        PlotEnumerator(),
        ForetellEnumerator(),
        SuspendEnumerator(),
        CastFromZoneEnumerator(),
        ManaAbilityEnumerator(),
        TurnFaceUpEnumerator(),
        UnlockRoomDoorEnumerator(),
        ActivatedAbilityEnumerator(),
        CrewEnumerator(),
        SaddleEnumerator(),
        ZoneActivatedAbilityEnumerator(Zone.GRAVEYARD),
        ZoneActivatedAbilityEnumerator(Zone.HAND),
        CommandZoneAbilityEnumerator()
    )

    /**
     * Enumerate all legal actions for the given player in the given state.
     *
     * @param state The current game state
     * @param playerId The player to enumerate actions for
     * @param mode Controls what data is computed. [EnumerationMode.ACTIONS_ONLY] skips
     *   auto-tap preview computation for simulation/MCTS use.
     * @return All legal actions (including unaffordable ones marked with affordable=false)
     */
    fun enumerate(
        state: GameState,
        playerId: EntityId,
        mode: EnumerationMode = EnumerationMode.FULL
    ): List<LegalAction> {
        val context = EnumerationContext(
            state = state,
            playerId = playerId,
            cardRegistry = cardRegistry,
            manaSolver = manaSolver,
            costCalculator = costCalculator,
            predicateEvaluator = predicateEvaluator,
            conditionEvaluator = conditionEvaluator,
            turnManager = turnManager,
            mode = mode
        )

        // Combat declaration steps are exclusive — only combat actions, no spells/abilities/pass
        if (combatEnumerator.isCombatDeclarationStep(context)) {
            return combatEnumerator.enumerate(context)
        }

        // Normal priority: enumerate all action categories
        val offers = enumerators.flatMap { it.enumerate(context) }
        // Split second (CR 702.61): while a spell with it is on the stack, withhold every spell and
        // non-mana activated ability — the same verdict ActionProcessor.validate reaches.
        val permitted = if (SplitSecond.isLocked(state, cardRegistry)) {
            offers.filterNot { SplitSecond.forbids(it.action, it.isManaAbility) }
        } else {
            offers
        }
        return com.wingedsheep.engine.legalactions.enumerators.AdditionalManaForCountersOffer
            .annotate(context, permitted)
    }

    /**
     * Enumerate only [playerId]'s mana abilities, ignoring priority.
     *
     * This is the CR 605.3a surface: while a rule or effect is asking a player for a mana payment
     * they hold no priority, but they may still activate mana abilities to produce the mana. See
     * [com.wingedsheep.engine.mechanics.mana.ManaPaymentWindow].
     */
    fun enumerateManaAbilities(
        state: GameState,
        playerId: EntityId,
        mode: EnumerationMode = EnumerationMode.FULL
    ): List<LegalAction> = ManaAbilityEnumerator().enumerate(
        EnumerationContext(
            state = state,
            playerId = playerId,
            cardRegistry = cardRegistry,
            manaSolver = manaSolver,
            costCalculator = costCalculator,
            predicateEvaluator = predicateEvaluator,
            conditionEvaluator = conditionEvaluator,
            turnManager = turnManager,
            mode = mode
        )
    )

    companion object {
        /**
         * Create a LegalActionEnumerator with the same dependencies as LegalActionsCalculator.
         */
        fun create(
            cardRegistry: CardRegistry,
            manaSolver: ManaSolver = ManaSolver(cardRegistry),
            costCalculator: CostCalculator = CostCalculator(cardRegistry),
            predicateEvaluator: PredicateEvaluator = PredicateEvaluator(),
            conditionEvaluator: ConditionEvaluator = ConditionEvaluator(),
            zones: ZoneTransitionService = ZoneTransitionService(cardRegistry),
            turnManager: TurnManager = TurnManager(
                zones,
                combatManager = com.wingedsheep.engine.mechanics.combat.CombatManager(
                    zones,
                    cardRegistry,
                    com.wingedsheep.engine.mechanics.mana.ManaAbilitySideEffectExecutor.noOp(zones)
                ),
                cardRegistry = cardRegistry
            )
        ): LegalActionEnumerator {
            return LegalActionEnumerator(
                cardRegistry = cardRegistry,
                manaSolver = manaSolver,
                costCalculator = costCalculator,
                predicateEvaluator = predicateEvaluator,
                conditionEvaluator = conditionEvaluator,
                turnManager = turnManager
            )
        }
    }
}
