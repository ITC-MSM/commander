package com.wingedsheep.engine.core

import com.wingedsheep.engine.legality.LegalityKernel
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.event.TriggerProcessor
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.ContinuationHandler
import com.wingedsheep.engine.handlers.CostHandler
import com.wingedsheep.engine.handlers.EffectHandler
import com.wingedsheep.engine.handlers.MulliganHandler
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.replacement.ReplacementEffectProcessor
import com.wingedsheep.engine.legalactions.utils.CastPermissionUtils
import com.wingedsheep.engine.handlers.effects.EffectExecutorRegistry
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.StateBasedActionChecker
import com.wingedsheep.engine.mechanics.combat.CombatManager
import com.wingedsheep.engine.mechanics.mana.AlternativePaymentHandler
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.mechanics.mana.GrantedKeywordResolver
import com.wingedsheep.engine.mechanics.mana.ManaAbilitySideEffectExecutor
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.mechanics.stack.StackResolver
import com.wingedsheep.engine.mechanics.targeting.TargetValidator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.registry.PrintingRegistry
import com.wingedsheep.engine.registry.TokenArtRegistry

/**
 * Composition root for the rules engine.
 *
 * Constructs and wires all engine services from a single [CardRegistry].
 * This eliminates duplicated wiring across ActionProcessor and GameSession,
 * and ensures all consumers share the same service instances.
 */
class EngineServices(
    val cardRegistry: CardRegistry,
    /**
     * Optional per-printing registry. Threaded into [GameInitializer] so deck entries with
     * pinned printings can override per-entity art at game-init. Null is fine — every
     * lookup is null-safe.
     */
    val printingRegistry: PrintingRegistry? = null,
    /**
     * Optional per-set token art. Threaded into the token executors so a created token shows the
     * art printed by the set of the card that created it. Null is fine — tokens then fall back to
     * the engine-wide generic art for their creature type.
     */
    val tokenArtRegistry: TokenArtRegistry? = null,
) {
    /**
     * The one zone-transition service for this engine. It carries the card and token-art
     * registries every zone move needs (battlefield-entry setup, a zone-change rider's token), so
     * it is threaded through the graph rather than parked in a global — two engines in one JVM
     * (server plus gym, parallel tests) each keep their own.
     */
    val zones = ZoneTransitionService(cardRegistry, tokenArtRegistry)

    /**
     * The one replacement-effect processor for this game. Declared before anything that
     * consumes it so the whole graph — the draw path via [EffectExecutorRegistry] and
     * [turnManager], and the continuation resumers — shares a single instance rather than
     * each constructing its own. The processor is stateless today; keeping it single is what
     * makes it safe for it to stop being so.
     */
    val replacementEffectProcessor = ReplacementEffectProcessor()
    val effectExecutorRegistry = EffectExecutorRegistry(
        zones,
        cardRegistry = cardRegistry,
        tokenArtRegistry = tokenArtRegistry,
        replacementProcessor = replacementEffectProcessor
    )
    val manaAbilitySideEffectExecutor = ManaAbilitySideEffectExecutor(
        zones,
        cardRegistry = cardRegistry,
        effectExecutor = effectExecutorRegistry::execute
    )
    val combatManager = CombatManager(zones, cardRegistry, manaAbilitySideEffectExecutor)
    val triggerDetector = TriggerDetector(cardRegistry)
    val stateTriggerPoller = com.wingedsheep.engine.event.StateTriggerPoller(cardRegistry)
    val stackResolver = StackResolver(
        zones,
        effectHandler = EffectHandler(zones, cardRegistry = cardRegistry, registry = effectExecutorRegistry),
        cardRegistry = cardRegistry
    )
    val triggerProcessor = TriggerProcessor(cardRegistry = cardRegistry, stackResolver = stackResolver)
    val manaSolver = ManaSolver(cardRegistry)
    val costCalculator = CostCalculator(cardRegistry)
    val grantedKeywordResolver = GrantedKeywordResolver(cardRegistry)
    val alternativePaymentHandler = AlternativePaymentHandler(grantedKeywordResolver)
    val costHandler = CostHandler(zones)
    val mulliganHandler = MulliganHandler(cardRegistry)
    val conditionEvaluator = ConditionEvaluator()
    val targetValidator = TargetValidator()
    val targetFinder = TargetFinder()
    val predicateEvaluator = PredicateEvaluator(cardRegistry)
    val castPermissionUtils = CastPermissionUtils(cardRegistry, predicateEvaluator, conditionEvaluator)
    val legalityKernel = LegalityKernel(cardRegistry, conditionEvaluator)
    val sbaChecker = StateBasedActionChecker(zones, cardRegistry = cardRegistry)
    val turnManager = TurnManager(
        zones,
        cardRegistry = cardRegistry,
        combatManager = combatManager,
        sbaChecker = sbaChecker,
        effectExecutor = effectExecutorRegistry::execute,
        replacementProcessor = replacementEffectProcessor
    )
    val continuationHandler = ContinuationHandler(this)
    val settler = Settler(
        triggerDetector, triggerProcessor, sbaChecker, stateTriggerPoller, turnManager,
        effectExecutor = effectExecutorRegistry::execute
    )

    init {
        // Late wiring: every service in the graph is now constructed, so it's safe to
        // hand `this` to executors that need to synthesize a `CastSpell` action through
        // the full cast pipeline (currently only
        // [com.wingedsheep.engine.handlers.effects.library.CastFromCollectionWithoutPayingCostExecutor]).
        effectExecutorRegistry.libraryExecutors.initialize(this)
    }
}
