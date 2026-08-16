package com.wingedsheep.engine.mechanics.combat

import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.mana.ManaPool
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.mechanics.mana.IntrinsicManaAbilities
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BlockedComponent
import com.wingedsheep.engine.state.components.combat.BlockedThisCombatComponent
import com.wingedsheep.engine.state.components.combat.BlockersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.combat.BlockingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.mechanics.combat.rules.BlockCheckContext
import com.wingedsheep.engine.mechanics.combat.rules.BlockEvasionRule
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.ManaSymbol
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.sdk.scripting.BlockTax
import com.wingedsheep.sdk.scripting.BlockerCountLimit
import com.wingedsheep.sdk.scripting.CanBlockAnyNumber
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.MustBeBlocked
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.scripting.CantBlockUnlessCoBlocker
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ManaColorSet
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.Scope
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import java.util.UUID

/**
 * Handles the declare blockers step of combat.
 *
 * Responsibilities:
 * - Validating individual blockers (creature eligibility, evasion, can't block)
 * - Menace requirements
 * - Must-be-blocked requirements (Alluring Scent, Taunting Elf)
 * - Provoke requirements
 * - Projected must-block requirements (Grand Melee)
 * - Block taxes (Whipgrass Entangler)
 * - Blocker order decisions for multiple blockers
 * - Mandatory blocker assignment queries
 */
internal class BlockPhaseManager(
    private val cardRegistry: CardRegistry,
    private val blockEvasionRules: List<BlockEvasionRule>,
    private val manaAbilitySideEffectExecutor: com.wingedsheep.engine.mechanics.mana.ManaAbilitySideEffectExecutor,
) {
    private val conditionEvaluator = ConditionEvaluator()
    private val dynamicAmountEvaluator = DynamicAmountEvaluator()
    private val predicateEvaluator = PredicateEvaluator()

    /**
     * Validate and declare blockers.
     *
     * @param blockers Map of blocker entity ID to list of attackers being blocked
     */
    fun declareBlockers(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>
    ): ExecutionResult {
        // Validate each blocker
        for ((blockerId, attackerIds) in blockers) {
            val validation = validateBlocker(state, blockingPlayer, blockerId, attackerIds)
            if (validation != null) {
                return ExecutionResult.error(state, validation)
            }
        }

        // Check menace requirements
        val menaceValidation = validateMenaceRequirements(state, blockers)
        if (menaceValidation != null) {
            return ExecutionResult.error(state, menaceValidation)
        }

        // Check "can't be blocked except by N or more creatures" (Troll of Khazad-dûm)
        val minBlockersValidation = validateMinBlockersRequirements(state, blockers)
        if (minBlockersValidation != null) {
            return ExecutionResult.error(state, minBlockersValidation)
        }

        // Check max-blocker restrictions on attackers (CantBeBlockedByMoreThan)
        val maxBlockersValidation = validateMaxBlockersRequirements(state, blockers)
        if (maxBlockersValidation != null) {
            return ExecutionResult.error(state, maxBlockersValidation)
        }

        // Check global blocker-count caps (Dueling Grounds — "No more than one creature can
        // block each combat"). Counts distinct blocking creatures across all players.
        val blockerCountValidation = validateGlobalBlockerCount(state, blockers.keys)
        if (blockerCountValidation != null) {
            return ExecutionResult.error(state, blockerCountValidation)
        }

        // Check co-blocker requirements (CR 509.1b — "can't block alone" / "can't block unless an
        // X also blocks"). Depends on the whole proposed blocker group, not the attacker, so it's
        // validated here rather than per-blocker. Mirrors the co-attacker check in declare-attackers.
        val coBlockerValidation = validateCoBlockerRequirements(state, state.projectedState, blockers.keys)
        if (coBlockerValidation != null) {
            return ExecutionResult.error(state, coBlockerValidation)
        }

        // Check "must be blocked" requirements (Alluring Scent, etc.)
        val mustBeBlockedValidation = validateMustBeBlockedRequirements(state, blockingPlayer, blockers)
        if (mustBeBlockedValidation != null) {
            return ExecutionResult.error(state, mustBeBlockedValidation)
        }

        // Check provoke "must block specific attacker" requirements
        val provokeValidation = validateProvokeRequirements(state, blockingPlayer, blockers)
        if (provokeValidation != null) {
            return ExecutionResult.error(state, provokeValidation)
        }

        // Check projected must-block requirements (Grand Melee)
        val projectedMustBlockValidation = validateProjectedMustBlockRequirements(state, blockingPlayer, blockers)
        if (projectedMustBlockValidation != null) {
            return ExecutionResult.error(state, projectedMustBlockValidation)
        }

        // Calculate each controller's fixed share without paying anything. In shared-team combat,
        // one representative submits the map but each teammate pays only for creatures they
        // control. The resulting amounts are carried through the decision chain and are never
        // recomputed between prompts.
        val projected = state.projectedState
        val blockerIdsByController = blockers.keys.groupBy { blockerId ->
            projected.getController(blockerId)!! // validateBlocker established a team controller
        }
        val taxByPayer = blockerIdsByController.mapValues { (_, payerBlockers) ->
            val ids = payerBlockers.toSet()
            calculatePerCreatureTax(state, ids, projected) + calculateBlockTax(state, ids, projected)
        }.filterValues { it > 0 }
        if (taxByPayer.isNotEmpty()) {
            return pauseForBlockTaxConfirmation(state, blockingPlayer, blockers, taxByPayer)
        }

        return commitBlockDeclaration(state, blockingPlayer, blockers, taxEvents = emptyList())
    }

    /**
     * Apply the post-tax commitment for a declared block: stamp [BlockingComponent] /
     * [BlockedComponent], mark the blockers-declared tracking component, emit the
     * [BlockersDeclaredEvent], and queue any blocker-order / attacker-order decisions.
     *
     * Callable from the synchronous (no-tax) path in [declareBlockers] and from
     * [com.wingedsheep.engine.handlers.continuations.CombatTaxContinuationResumer] after
     * the player confirms the tax.
     */
    internal fun commitBlockDeclaration(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>,
        taxEvents: List<com.wingedsheep.engine.core.GameEvent>,
    ): ExecutionResult {
        // CR 702.22h: blocking any member of an attacking band blocks the whole band — a blocker
        // assigned to one band member is treated as blocking every member. Expand the declared
        // assignments before stamping so the rest of combat (ordering, the damage board) sees the
        // full bipartite picture.
        val bandMembers = collectBands(state)
        val expandedBlockers: Map<EntityId, List<EntityId>> = blockers.mapValues { (_, attackerIds) ->
            val expanded = LinkedHashSet<EntityId>()
            for (attackerId in attackerIds) {
                expanded += attackerId
                val bandId = state.getEntity(attackerId)?.get<AttackingComponent>()?.bandId
                if (bandId != null) expanded += bandMembers[bandId] ?: emptySet()
            }
            expanded.toList()
        }

        var newState = state
        // Capture legendary-ness of every combatant *now* (at block declaration), so the
        // "blocked or was blocked by a legendary creature this turn" marker (You Cannot Pass!)
        // reflects the pairing-time status even if a legendary partner later leaves or loses
        // legendary-ness (CR: the predicate looks at combat history).
        val projected = state.projectedState
        for ((blockerId, attackerIds) in expandedBlockers) {
            newState = newState.updateEntity(blockerId) { container ->
                container.with(BlockingComponent(attackerIds))
                    .with(BlockedThisCombatComponent)
            }

            // Mark attackers as blocked
            for (attackerId in attackerIds) {
                newState = newState.updateEntity(attackerId) { container ->
                    val existing = container.get<BlockedComponent>()?.blockerIds ?: emptyList()
                    container.with(BlockedComponent(existing + blockerId))
                }
            }

            // Stamp the "paired with a legendary in combat this turn" marker on each side
            // whose partner is legendary.
            val blockerIsLegendary = projected.isLegendary(blockerId)
            for (attackerId in attackerIds) {
                if (projected.isLegendary(attackerId)) {
                    newState = newState.updateEntity(blockerId) { container ->
                        container.with(com.wingedsheep.engine.state.components.combat.BlockedOrWasBlockedByLegendaryThisTurnComponent)
                    }
                }
                if (blockerIsLegendary) {
                    newState = newState.updateEntity(attackerId) { container ->
                        container.with(com.wingedsheep.engine.state.components.combat.BlockedOrWasBlockedByLegendaryThisTurnComponent)
                    }
                }
            }
        }

        // CR 805.10d: this is one combined declaration for the defending shared-turn team.
        // Mark every teammate so the APNAP coordinator cannot request a second declaration.
        for (defenderId in state.sharedTurnTeam(blockingPlayer)) {
            newState = newState.updateEntity(defenderId) { container ->
                container.with(BlockersDeclaredThisCombatComponent)
            }
        }

        val blockerNameMap = expandedBlockers.keys.associateWith { state.getEntity(it)?.get<CardComponent>()?.name ?: "Creature" }
        val attackerNameMap = expandedBlockers.values.flatten().distinct().associateWith { state.getEntity(it)?.get<CardComponent>()?.name ?: "Creature" }
        val blockersEvent = BlockersDeclaredEvent(expandedBlockers, blockerNameMap, attackerNameMap)
        val blockTaxEvents = taxEvents

        // Damage-assignment order (CR 510.1c/d) is no longer collected in a standalone
        // OrderObjectsDecision pre-step. The combat resolution board owns ordering: it reads the
        // declaration order (BlockedComponent.blockerIds / BlockingComponent.blockedAttackerIds)
        // as the default and lets the chooser reorder via the response. No pause here.
        return ExecutionResult.success(
            newState,
            blockTaxEvents + blockersEvent
        )
    }

    /**
     * Collect the current attacking bands, keyed by [AttackingComponent.bandId]. Used to expand
     * declared block assignments so a blocker on one band member blocks the whole band (CR 702.22h).
     */
    private fun collectBands(state: GameState): Map<String, Set<EntityId>> {
        val result = mutableMapOf<String, MutableSet<EntityId>>()
        for ((entityId, container) in state.entities) {
            val bandId = container.get<AttackingComponent>()?.bandId ?: continue
            result.getOrPut(bandId) { mutableSetOf() }.add(entityId)
        }
        return result
    }

    /**
     * Check if a creature can legally block at least one of the current attackers.
     */
    fun canCreatureBlockAnyAttacker(state: GameState, blockerId: EntityId, blockingPlayer: EntityId): Boolean {
        val blockerContainer = state.getEntity(blockerId) ?: return false
        val blockerCard = blockerContainer.get<CardComponent>() ?: return false

        val isFaceDown = blockerContainer.has<FaceDownComponent>()
        if (!isFaceDown && hasCantBlockAbility(blockerCard)) return false

        val projected = state.projectedState

        if (projected.cantBlock(blockerId)) return false

        if (!isFaceDown && hasCantBlockUnlessRestriction(state, blockerId, blockingPlayer, projected)) return false

        val attackers = state.entities.filter { (_, container) -> container.has<AttackingComponent>() }.keys

        return attackers.any { attackerId ->
            canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)
        }
    }

    /**
     * Compute mandatory blocker assignments from floating effects.
     * Returns a map of blocker → list of attackers it must block.
     */
    fun getMandatoryBlockerAssignments(state: GameState, blockingPlayer: EntityId): Map<EntityId, List<EntityId>> {
        val projected = state.projectedState
        val potentialBlockers = findPotentialBlockers(state, blockingPlayer)
        val result = mutableMapOf<EntityId, MutableList<EntityId>>()

        // 1. MustBlockSpecificAttacker (Provoke)
        val provokeConstraints = state.floatingEffects
            .filter { it.effect.modification is SerializableModification.MustBlockSpecificAttacker }
            .flatMap { floatingEffect ->
                val modification = floatingEffect.effect.modification as SerializableModification.MustBlockSpecificAttacker
                floatingEffect.effect.affectedEntities.map { blockerId ->
                    blockerId to modification.attackerId
                }
            }

        for ((blockerId, attackerId) in provokeConstraints) {
            if (blockerId !in potentialBlockers) continue
            val controller = projected.getController(blockerId)
            if (controller !in state.sharedTurnTeam(blockingPlayer)) continue
            val attackerContainer = state.getEntity(attackerId) ?: continue
            if (!attackerContainer.has<AttackingComponent>()) continue
            if (!canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)) continue
            result.getOrPut(blockerId) { mutableListOf() }.add(attackerId)
        }

        // 2. MustBeBlockedByAll (Taunting Elf, Alluring Scent)
        val mustBeBlockedAttackers = findMustBeBlockedAttackers(state)
        for (attackerId in mustBeBlockedAttackers) {
            for (blockerId in potentialBlockers) {
                if (canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)) {
                    result.getOrPut(blockerId) { mutableListOf() }.add(attackerId)
                }
            }
        }

        return result.filterValues { it.isNotEmpty() }
    }

    // =========================================================================
    // Blocker Validation
    // =========================================================================

    /**
     * Validate that a creature can block.
     */
    private fun validateBlocker(
        state: GameState,
        blockingPlayer: EntityId,
        blockerId: EntityId,
        attackerIds: List<EntityId>
    ): String? {
        val container = state.getEntity(blockerId)
            ?: return "Blocker not found: $blockerId"

        val cardComponent = container.get<CardComponent>()
            ?: return "Not a card: $blockerId"

        val projected = state.projectedState

        if (!projected.isCreature(blockerId)) {
            return "Only creatures can block: ${cardComponent.name}"
        }
        val controller = projected.getController(blockerId)
        if (controller !in state.sharedTurnTeam(blockingPlayer)) {
            return "You don't control ${cardComponent.name}"
        }

        if (container.has<TappedComponent>()) {
            return "${cardComponent.name} is tapped and cannot block"
        }

        if (container.has<BlockingComponent>()) {
            return "${cardComponent.name} is already blocking"
        }

        val isFaceDown = container.has<FaceDownComponent>()
        if (!isFaceDown) {
            val cantBlockValidation = validateCantBlock(cardComponent)
            if (cantBlockValidation != null) {
                return cantBlockValidation
            }
        }

        if (projected.cantBlock(blockerId)) {
            return "${cardComponent.name} can't block"
        }

        if (!isFaceDown) {
            val cantBlockUnlessError = validateCantBlockUnless(state, blockerId, controller ?: blockingPlayer, projected)
            if (cantBlockUnlessError != null) return cantBlockUnlessError
        }

        if (attackerIds.size > 1) {
            val canBlockAny = if (!isFaceDown) {
                val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId)
                cardDef?.staticAbilities?.any { it is CanBlockAnyNumber } == true
            } else false
            if (!canBlockAny) {
                val additionalBlocks = projected.getAdditionalBlockCount(blockerId)
                val maxBlocks = 1 + additionalBlocks
                if (attackerIds.size > maxBlocks) {
                    val countText = if (maxBlocks == 1) "one creature" else "$maxBlocks creatures"
                    return "${cardComponent.name} can only block $countText"
                }
            }
        }

        // Check each attacker
        for (attackerId in attackerIds) {
            // CR 509.1b / 805.10d: a creature can only block an attacker that is attacking its
            // controller (or a planeswalker/battle its controller protects). Under shared team turns
            // (Two-Headed Giant) the defending team blocks as one, so a creature may block an attacker
            // aimed at any teammate; without shared team turns (Team vs. Team — CR 808, non-team
            // games) sharedTurnTeam is a singleton, so you can only block attackers aimed at you.
            val attacking = state.getEntity(attackerId)?.get<AttackingComponent>()
                ?: return "${cardComponent.name} can't block: ${attackerId.value} isn't attacking"
            val attackedDefender = CombatDefenders.defendingPlayerOf(state, attacking.defenderId)
            if (attackedDefender !in state.sharedTurnTeam(blockingPlayer)) {
                return "${cardComponent.name} can't block a creature attacking another player"
            }

            val evasionValidation = validateCanBlock(state, blockerId, attackerId, controller ?: blockingPlayer)
            if (evasionValidation != null) {
                return evasionValidation
            }
        }

        return null
    }

    /**
     * Validate that a blocker can block a specific attacker (evasion abilities).
     * Delegates to registered [BlockEvasionRule] instances.
     */
    private fun validateCanBlock(
        state: GameState,
        blockerId: EntityId,
        attackerId: EntityId,
        blockingPlayer: EntityId
    ): String? {
        state.getEntity(attackerId) ?: return "Attacker not found: $attackerId"
        state.getEntity(attackerId)?.get<CardComponent>() ?: return "Not a card: $attackerId"

        val ctx = BlockCheckContext(
            state = state,
            projected = state.projectedState,
            attackerId = attackerId,
            blockerId = blockerId,
            blockingPlayer = blockingPlayer,
            cardRegistry = cardRegistry
        )
        for (rule in blockEvasionRules) {
            val error = rule.check(ctx)
            if (error != null) return error
        }
        return null
    }

    /**
     * Check if a creature has "can't block" ability (e.g., Craven Giant, Jungle Lion).
     */
    private fun validateCantBlock(blockerCard: CardComponent): String? {
        val cardDef = cardRegistry.getCard(blockerCard.cardDefinitionId) ?: return null
        val cantBlockAbility = cardDef.staticAbilities.filterIsInstance<CantBlock>().firstOrNull()
            ?: return null

        if (cantBlockAbility.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self) {
            return "${blockerCard.name} can't block"
        }

        return null
    }

    /**
     * Check if a creature has "can't block" ability.
     * Returns true if the creature cannot block.
     */
    private fun hasCantBlockAbility(blockerCard: CardComponent): Boolean {
        val cardDef = cardRegistry.getCard(blockerCard.cardDefinitionId) ?: return false
        val cantBlockAbility = cardDef.staticAbilities.filterIsInstance<CantBlock>().firstOrNull()
            ?: return false

        return cantBlockAbility.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self
    }

    /**
     * Check if a creature can legally block an attacker.
     * Delegates to registered [BlockEvasionRule] instances for evasion checks,
     * plus blocker-level restrictions (can't block, face-down abilities).
     */
    private fun canCreatureBlockAttacker(
        state: GameState,
        blockerId: EntityId,
        attackerId: EntityId,
        blockingPlayer: EntityId,
        projected: ProjectedState
    ): Boolean {
        val blockerContainer = state.getEntity(blockerId) ?: return false
        state.getEntity(attackerId) ?: return false

        val blockerCard = blockerContainer.get<CardComponent>() ?: return false

        val isFaceDown = blockerContainer.has<FaceDownComponent>()
        if (!isFaceDown && hasCantBlockAbility(blockerCard)) {
            return false
        }

        if (projected.cantBlock(blockerId)) {
            return false
        }

        val ctx = BlockCheckContext(
            state = state,
            projected = projected,
            attackerId = attackerId,
            blockerId = blockerId,
            // Shared-team validation may be initiated by either defender. Evasion and any
            // controller-relative restriction belong to the creature's actual controller.
            blockingPlayer = projected.getController(blockerId) ?: blockingPlayer,
            cardRegistry = cardRegistry
        )
        return blockEvasionRules.all { it.check(ctx) == null }
    }

    // =========================================================================
    // Menace
    // =========================================================================

    /**
     * Validate menace requirements (must be blocked by 2+ creatures).
     */
    private fun validateMenaceRequirements(
        state: GameState,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val attackerToBlockers = mutableMapOf<EntityId, MutableList<EntityId>>()
        for ((blockerId, attackerIds) in blockers) {
            for (attackerId in attackerIds) {
                attackerToBlockers.getOrPut(attackerId) { mutableListOf() }.add(blockerId)
            }
        }

        val projected = state.projectedState

        for ((attackerId, blockerList) in attackerToBlockers) {
            val attackerContainer = state.getEntity(attackerId) ?: continue
            val attackerCard = attackerContainer.get<CardComponent>() ?: continue

            if (projected.hasKeyword(attackerId, Keyword.MENACE)) {
                if (blockerList.size < 2) {
                    return "${attackerCard.name} has menace and must be blocked by 2 or more creatures"
                }
            }
        }

        return null
    }

    /**
     * Validate "can't be blocked except by N or more creatures" ([CantBeBlockedByFewerThan]).
     * Generalizes menace: an attacker carrying the static may be left unblocked, but if blocked it
     * must have at least [CantBeBlockedByFewerThan.minBlockers] blockers.
     */
    private fun validateMinBlockersRequirements(
        state: GameState,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val attackerToBlockers = mutableMapOf<EntityId, MutableList<EntityId>>()
        for ((blockerId, attackerIds) in blockers) {
            for (attackerId in attackerIds) {
                attackerToBlockers.getOrPut(attackerId) { mutableListOf() }.add(blockerId)
            }
        }

        for ((attackerId, blockerList) in attackerToBlockers) {
            if (blockerList.isEmpty()) continue
            val attackerContainer = state.getEntity(attackerId) ?: continue
            if (attackerContainer.has<FaceDownComponent>()) continue
            val attackerCard = attackerContainer.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(attackerCard.cardDefinitionId) ?: continue

            val minBlockers = cardDef.staticAbilities
                .filterIsInstance<com.wingedsheep.sdk.scripting.CantBeBlockedByFewerThan>()
                .filter { it.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self }
                .maxOfOrNull { it.minBlockers } ?: continue

            if (blockerList.size < minBlockers) {
                return "${attackerCard.name} can't be blocked except by $minBlockers or more creatures"
            }
        }

        return null
    }

    /**
     * Validate `CantBeBlockedByMoreThan` restrictions (CR 509.1b).
     * Each attacker with this static ability caps the number of creatures that may block it.
     */
    private fun validateMaxBlockersRequirements(
        state: GameState,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val attackerToBlockerCount = mutableMapOf<EntityId, Int>()
        for (attackerIds in blockers.values) {
            for (attackerId in attackerIds) {
                attackerToBlockerCount.merge(attackerId, 1, Int::plus)
            }
        }

        for ((attackerId, count) in attackerToBlockerCount) {
            val attackerContainer = state.getEntity(attackerId) ?: continue
            if (attackerContainer.has<FaceDownComponent>()) continue
            val attackerCard = attackerContainer.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(attackerCard.cardDefinitionId)

            // Printed "can't be blocked by more than N", including the conditional form
            // (Akawalli's descend-8 "can't be blocked by more than one creature") — unwrap a
            // ConditionalStaticAbility and honor it only while its condition currently holds,
            // mirroring the MustBeBlocked handling in attackersWithMustBeBlockedStatic. cardDef
            // may be null for tokens/copies without a registered definition — the granted forms
            // below still apply.
            val attackerController = state.projectedState.getController(attackerId)
            val staticLimit = cardDef?.staticAbilities
                ?.mapNotNull { ability ->
                    val unwrapped = if (ability is ConditionalStaticAbility) ability.ability else ability
                    if (unwrapped !is CantBeBlockedByMoreThan) return@mapNotNull null
                    if (unwrapped.filter.scope !is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self) {
                        return@mapNotNull null
                    }
                    if (ability is ConditionalStaticAbility) {
                        if (attackerController == null) return@mapNotNull null
                        if (!conditionEvaluator.evaluate(
                                state,
                                ability.condition,
                                EffectContext(sourceId = attackerId, controllerId = attackerController)
                            )
                        ) return@mapNotNull null
                    }
                    unwrapped.maxBlockers
                }
                ?.minOrNull()
            // Granted static-ability form: e.g. Full Steam Ahead grants CantBeBlockedByMoreThan(1)
            // until end of turn via grantedStaticAbilities.
            val grantedLimit = state.grantedStaticAbilities
                .filter { it.entityId == attackerId }
                .map { it.ability }
                .filterIsInstance<CantBeBlockedByMoreThan>()
                .filter { it.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self }
                .minOfOrNull { it.maxBlockers }
            // Granted (floating) flag form (CR 509.1b): a temporary "can't be blocked by more than one
            // creature" via Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED_BY_MORE_THAN_ONE) caps at 1.
            val flagLimit = if (
                state.projectedState.hasKeyword(
                    attackerId,
                    com.wingedsheep.sdk.core.AbilityFlag.CANT_BE_BLOCKED_BY_MORE_THAN_ONE
                )
            ) 1 else null
            val limit = listOfNotNull(staticLimit, grantedLimit, flagLimit).minOrNull() ?: continue

            if (count > limit) {
                val countText = if (limit == 1) "more than one creature" else "more than $limit creatures"
                return "${attackerCard.name} can't be blocked by $countText"
            }
        }
        return null
    }

    /**
     * Validate global blocker-count caps. While any permanent with [BlockerCountLimit] is on the
     * battlefield (e.g. Dueling Grounds), the total number of distinct blocking creatures across
     * all players may not exceed the smallest such cap. Returns an error message when violated.
     */
    private fun validateGlobalBlockerCount(
        state: GameState,
        blockerIds: Set<EntityId>
    ): String? {
        var cap: Int? = null
        var capDescription = ""
        for (permId in state.getBattlefield()) {
            val cardComponent = state.getEntity(permId)?.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: continue
            for (ability in cardDef.staticAbilities.filterIsInstance<BlockerCountLimit>()) {
                if (cap == null || ability.maxBlockers < cap) {
                    cap = ability.maxBlockers
                    capDescription = ability.description
                }
            }
        }
        if (cap != null && blockerIds.size > cap) {
            return capDescription
        }
        return null
    }

    /**
     * Validate "can't block unless [X] also blocks" restrictions ([CantBlockUnlessCoBlocker], CR
     * 509.1b). The blocking sibling of [com.wingedsheep.engine.mechanics.combat.AttackPhaseManager]'s
     * co-attacker check.
     *
     * For each proposed blocker carrying the restriction, at least one *other* blocker in the same
     * declaration must match the restriction's filter (evaluated with projected state so
     * color/type-changing effects are honored). The co-blocker need not block the same attacker —
     * it just has to be declared as a blocker this combat. Self never counts as its own co-blocker.
     *
     * Restrictions are read from both the card definition (printed) and grantedStaticAbilities, so
     * the form arrives on a token without a CardDefinition (Toby's Beast token — "This token can't
     * attack or block alone").
     */
    private fun validateCoBlockerRequirements(
        state: GameState,
        projected: ProjectedState,
        blockerIds: Set<EntityId>
    ): String? {
        for (blockerId in blockerIds) {
            val cardComponent = state.getEntity(blockerId)?.get<CardComponent>() ?: continue
            if (state.getEntity(blockerId)?.has<FaceDownComponent>() == true) continue
            val printed = cardRegistry.getCard(cardComponent.cardDefinitionId)
                ?.staticAbilities.orEmpty()
            val granted = state.grantedStaticAbilities
                .filter { it.entityId == blockerId }
                .map { it.ability }
            val restrictions = (printed + granted)
                .filterIsInstance<CantBlockUnlessCoBlocker>()
                .filter { it.filter.scope is Scope.Self }
            for (restriction in restrictions) {
                val context = PredicateContext(controllerId = projected.getController(blockerId) ?: blockerId)
                val satisfied = blockerIds.any { otherId ->
                    otherId != blockerId &&
                        predicateEvaluator.matches(state, projected, otherId, restriction.coBlockerFilter, context)
                }
                if (!satisfied) {
                    return "${cardComponent.name} ${restriction.description}"
                }
            }
        }
        return null
    }

    // =========================================================================
    // Must Be Blocked Requirements
    // =========================================================================

    /**
     * Validate "must be blocked" requirements.
     * Handles both "must be blocked by all" (Lure) and "must be blocked if able" (Gaea's Protector).
     */
    private fun validateMustBeBlockedRequirements(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val projected = state.projectedState
        val potentialBlockers = findPotentialBlockers(state, blockingPlayer)

        // Build reverse map: attacker → set of blockers assigned to it
        val attackerToBlockers = mutableMapOf<EntityId, MutableSet<EntityId>>()
        for ((blockerId, attackerIds) in blockers) {
            for (attackerId in attackerIds) {
                attackerToBlockers.getOrPut(attackerId) { mutableSetOf() }.add(blockerId)
            }
        }

        // 1. "Must be blocked by all" (Lure/Taunting Elf): every blocker that CAN block it MUST block it
        val mustBeBlockedByAllAttackers = findMustBeBlockedAttackers(state)
        if (mustBeBlockedByAllAttackers.isNotEmpty()) {
            val blockerToAttackers = blockers.mapValues { it.value.toSet() }

            for (blockerId in potentialBlockers) {
                val canBlockThese = mustBeBlockedByAllAttackers.filter { attackerId ->
                    canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)
                }

                if (canBlockThese.isEmpty()) {
                    continue
                }

                val actuallyBlocking = blockerToAttackers[blockerId] ?: emptySet()
                val blockingMustBeBlocked = actuallyBlocking.intersect(mustBeBlockedByAllAttackers.toSet())

                if (blockingMustBeBlocked.isEmpty()) {
                    val blockerCard = state.getEntity(blockerId)?.get<CardComponent>()
                    val blockerName = blockerCard?.name ?: "Creature"

                    val attackerNames = canBlockThese.mapNotNull { attackerId ->
                        state.getEntity(attackerId)?.get<CardComponent>()?.name
                    }

                    return if (canBlockThese.size == 1) {
                        "$blockerName must block ${attackerNames.first()}"
                    } else {
                        "$blockerName must block one of: ${attackerNames.joinToString(", ")}"
                    }
                }
            }
        }

        // 2. "Must be blocked if able" (Gaea's Protector): at least one creature must block it.
        // Rule 509.1c: the declaration is illegal if the number of requirements being obeyed is
        // fewer than the maximum number that could be obeyed. That maximum is a maximum bipartite
        // matching between the must-be-blocked attackers and the blockers hypothetically free to
        // cover them: a provoke-pinned blocker is only free for its pinned attacker, and a blocker
        // that can block a Lure-style attacker is claimed by that requirement (section 1 forces it
        // there). Per-pair blocking restrictions go through canCreatureBlockAttacker; declaration-
        // wide restrictions (e.g. can't-block-alone) are not modelled, so the computed maximum can
        // only over-count in those corners — never rejecting more than 509.1c would.
        val mustBeBlockedIfAbleAttackers = findMustBeBlockedIfAbleAttackers(state)
        if (mustBeBlockedIfAbleAttackers.isNotEmpty()) {
            val provokePinnedAttackers = state.floatingEffects
                .filter { it.effect.modification is SerializableModification.MustBlockSpecificAttacker }
                .flatMap { floatingEffect ->
                    val modification =
                        floatingEffect.effect.modification as SerializableModification.MustBlockSpecificAttacker
                    floatingEffect.effect.affectedEntities.map { it to modification.attackerId }
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { it.value.toSet() }
            val lureClaimedBlockers = potentialBlockers.filter { blockerId ->
                mustBeBlockedByAllAttackers.any { attackerId ->
                    canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)
                }
            }.toSet()

            fun canHypotheticallyBlock(blockerId: EntityId, attackerId: EntityId): Boolean {
                if (blockerId in lureClaimedBlockers) return false
                provokePinnedAttackers[blockerId]?.let { pins -> if (attackerId !in pins) return false }
                return canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)
            }

            // Maximum bipartite matching (attackers ↔ hypothetically-free blockers): its size is
            // the most requirements that could be simultaneously obeyed. Shared Kuhn's routine.
            val matchedAttackerOfBlocker = com.wingedsheep.engine.mechanics.BipartiteMatching
                .maximumMatching(mustBeBlockedIfAbleAttackers, potentialBlockers) { attackerId, blockerId ->
                    canHypotheticallyBlock(blockerId, attackerId)
                }
            val maxSatisfiable = matchedAttackerOfBlocker.size
            val satisfied = mustBeBlockedIfAbleAttackers.count { !attackerToBlockers[it].isNullOrEmpty() }

            if (satisfied < maxSatisfiable) {
                val matchedAttackers = matchedAttackerOfBlocker.values.toSet()
                val culpritId = mustBeBlockedIfAbleAttackers.first {
                    it in matchedAttackers && attackerToBlockers[it].isNullOrEmpty()
                }
                val attackerName = state.getEntity(culpritId)?.get<CardComponent>()?.name ?: "Creature"
                return "$attackerName must be blocked if able"
            }
        }

        return null
    }

    /**
     * Validate provoke "must block specific attacker" requirements.
     */
    private fun validateProvokeRequirements(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val projected = state.projectedState

        val provokeConstraints = state.floatingEffects
            .filter { it.effect.modification is SerializableModification.MustBlockSpecificAttacker }
            .flatMap { floatingEffect ->
                val modification = floatingEffect.effect.modification as SerializableModification.MustBlockSpecificAttacker
                floatingEffect.effect.affectedEntities.map { blockerId ->
                    blockerId to modification.attackerId
                }
            }

        for ((blockerId, attackerId) in provokeConstraints) {
            val controller = projected.getController(blockerId)
            if (controller !in state.sharedTurnTeam(blockingPlayer)) continue

            val blockerContainer = state.getEntity(blockerId) ?: continue
            if (blockerId !in state.getBattlefield()) continue
            if (blockerContainer.has<TappedComponent>()) continue

            val attackerContainer = state.getEntity(attackerId) ?: continue
            if (!attackerContainer.has<AttackingComponent>()) continue

            if (!canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)) continue

            val actuallyBlocking = blockers[blockerId] ?: emptyList()
            if (attackerId !in actuallyBlocking) {
                val blockerName = blockerContainer.get<CardComponent>()?.name ?: "Creature"
                val attackerName = attackerContainer.get<CardComponent>()?.name ?: "creature"
                return "$blockerName must block $attackerName (provoke)"
            }
        }

        return null
    }

    /**
     * Validate projected "must block" requirements (e.g., from Grand Melee).
     */
    private fun validateProjectedMustBlockRequirements(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>
    ): String? {
        val projected = state.projectedState
        val potentialBlockers = findPotentialBlockers(state, blockingPlayer)

        for (blockerId in potentialBlockers) {
            if (!projected.mustBlock(blockerId)) continue

            val attackers = state.findEntitiesWith<AttackingComponent>().map { it.first }
            val canBlockAny = attackers.any { attackerId ->
                canCreatureBlockAttacker(state, blockerId, attackerId, blockingPlayer, projected)
            }

            if (!canBlockAny) continue

            if (blockerId !in blockers.keys) {
                val cardName = state.getEntity(blockerId)?.get<CardComponent>()?.name ?: "Creature"
                return "$cardName must block this combat if able"
            }
        }

        return null
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Find all attackers that have "must be blocked by all" requirement active (Lure effects).
     */
    private fun findMustBeBlockedAttackers(state: GameState): List<EntityId> {
        val attackers = state.findEntitiesWith<AttackingComponent>().map { it.first }.toSet()

        val fromFloating = state.floatingEffects
            .filter { floatingEffect ->
                floatingEffect.effect.modification is SerializableModification.MustBeBlockedByAll
            }
            .flatMap { floatingEffect ->
                floatingEffect.effect.affectedEntities.filter { it in attackers }
            }
        return (fromFloating + attackersWithMustBeBlockedStatic(state, allCreatures = true)).distinct()
    }

    /**
     * Attackers that carry a [MustBeBlocked] static ability (matching [allCreatures]), including the
     * conditional form (e.g. Frodo Baggins: gated on `SourceIsRingBearer`). The gating condition is
     * evaluated with the attacker as the source.
     */
    private fun attackersWithMustBeBlockedStatic(state: GameState, allCreatures: Boolean): List<EntityId> {
        val attackers = state.findEntitiesWith<AttackingComponent>().map { it.first }
        if (attackers.isEmpty()) return emptyList()
        val projected = state.projectedState
        val attackerSet = attackers.toSet()
        val result = mutableSetOf<EntityId>()

        // (a) An attacker's own source-scoped MustBeBlocked static (filter == null), including the
        // conditional form (Frodo Baggins, gated on SourceIsRingBearer).
        for (attackerId in attackers) {
            val cardName = state.getEntity(attackerId)?.get<CardComponent>()?.cardDefinitionId ?: continue
            val statics = cardRegistry.getCard(cardName)?.staticAbilities.orEmpty()
            val active = statics.any { ability ->
                val unwrapped = if (ability is ConditionalStaticAbility) ability.ability else ability
                if (unwrapped !is MustBeBlocked || unwrapped.filter != null || unwrapped.allCreatures != allCreatures) {
                    return@any false
                }
                if (ability is ConditionalStaticAbility) {
                    val controller = projected.getController(attackerId) ?: return@any false
                    conditionEvaluator.evaluate(
                        state,
                        ability.condition,
                        EffectContext(sourceId = attackerId, controllerId = controller)
                    )
                } else true
            }
            if (active) result.add(attackerId)
        }

        // (b) A battlefield permanent projecting MustBeBlocked onto a *different* creature via a
        // filter - e.g. an Equipment granting "equipped creature ... must be blocked if able"
        // (The Masamune, filter = GroupFilter.attachedCreature()). The filter is resolved relative
        // to the permanent carrying the static.
        for (sourceId in state.getBattlefield()) {
            val container = state.getEntity(sourceId) ?: continue
            if (container.has<FaceDownComponent>()) continue
            val cardName = container.get<CardComponent>()?.cardDefinitionId ?: continue
            val statics = cardRegistry.getCard(cardName)?.staticAbilities.orEmpty()
            for (ability in statics) {
                val unwrapped = if (ability is ConditionalStaticAbility) ability.ability else ability
                if (unwrapped !is MustBeBlocked || unwrapped.allCreatures != allCreatures) continue
                val filter = unwrapped.filter ?: continue
                val controller = projected.getController(sourceId) ?: continue
                if (ability is ConditionalStaticAbility &&
                    !conditionEvaluator.evaluate(
                        state, ability.condition, EffectContext(sourceId = sourceId, controllerId = controller)
                    )
                ) continue
                result.addAll(
                    resolveFilteredMustBeBlockedAttackers(state, projected, sourceId, controller, filter, attackerSet)
                )
            }
        }

        return result.toList()
    }

    /**
     * Resolve which declared attackers a filtered [MustBeBlocked] static (carried by [sourceId])
     * applies to. Source-relative scopes resolve against [sourceId]: `AttachedTo` -> the creature it
     * is attached to (equipped creature), `Self` -> the source, `Specific` -> the bound entity;
     * `Battlefield` matches every attacker against the base filter. Only attackers pass, and each
     * must also satisfy the base filter (evaluated with the static's source as context).
     */
    private fun resolveFilteredMustBeBlockedAttackers(
        state: GameState,
        projected: ProjectedState,
        sourceId: EntityId,
        controllerId: EntityId,
        filter: GroupFilter,
        attackerSet: Set<EntityId>,
    ): List<EntityId> {
        val candidates: List<EntityId> = when (val scope = filter.scope) {
            is Scope.AttachedTo -> listOfNotNull(state.getEntity(sourceId)?.get<AttachedToComponent>()?.targetId)
            is Scope.Self -> listOf(sourceId)
            is Scope.SoulbondPair ->
                com.wingedsheep.engine.mechanics.SoulbondPairing.pairOf(state, sourceId).toList()
            is Scope.Specific -> listOf(scope.entityId)
            is Scope.Battlefield -> attackerSet.toList()
        }
        return candidates.filter { id ->
            id in attackerSet &&
                predicateEvaluator.matches(
                    state, projected, id, filter.baseFilter,
                    PredicateContext(sourceId = sourceId, controllerId = controllerId)
                )
        }
    }

    /**
     * Find all attackers that have "must be blocked if able" requirement active.
     * These only require at least one blocker, not all.
     */
    private fun findMustBeBlockedIfAbleAttackers(state: GameState): List<EntityId> {
        val attackers = state.findEntitiesWith<AttackingComponent>().map { it.first }.toSet()

        val fromFloating = state.floatingEffects
            .filter { floatingEffect ->
                floatingEffect.effect.modification is SerializableModification.MustBeBlockedIfAble
            }
            .flatMap { floatingEffect ->
                floatingEffect.effect.affectedEntities.filter { it in attackers }
            }
        return (fromFloating + attackersWithMustBeBlockedStatic(state, allCreatures = false)).distinct()
    }

    /**
     * Find all potential blockers (untapped creatures controlled by the blocking player).
     */
    private fun findPotentialBlockers(state: GameState, blockingPlayer: EntityId): List<EntityId> {
        val projected = state.projectedState
        return state.getBattlefield()
            .filter { entityId ->
                val container = state.getEntity(entityId) ?: return@filter false
                container.get<CardComponent>() ?: return@filter false
                val controller = projected.getController(entityId)

                projected.isCreature(entityId) &&
                    controller in state.sharedTurnTeam(blockingPlayer) &&
                    !container.has<TappedComponent>()
            }
    }

    // =========================================================================
    // CantBlockUnless
    // =========================================================================

    /**
     * Validate CantBlockUnless restrictions for a blocker.
     */
    private fun validateCantBlockUnless(
        state: GameState,
        blockerId: EntityId,
        blockingPlayer: EntityId,
        projected: ProjectedState
    ): String? {
        val container = state.getEntity(blockerId) ?: return null
        if (container.has<FaceDownComponent>()) return null
        val cardComponent = container.get<CardComponent>() ?: return null
        val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: return null

        val restriction = cardDef.staticAbilities
            .filterIsInstance<CantBlockUnless>()
            .firstOrNull { it.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self } ?: return null

        val attackers = state.entities.filter { (_, c) -> c.has<AttackingComponent>() }
        if (attackers.isEmpty()) return null

        val anyAttacker = attackers.keys.first()
        val attackingPlayer = projected.getController(anyAttacker) ?: return null

        val effectContext = EffectContext(
            sourceId = blockerId,
            controllerId = blockingPlayer,
        )
        if (!conditionEvaluator.evaluate(state, restriction.condition, effectContext)) {
            return "${cardComponent.name} ${restriction.description}"
        }

        return null
    }

    /**
     * Check if a creature has a CantBlockUnless restriction.
     */
    private fun hasCantBlockUnlessRestriction(
        state: GameState,
        blockerId: EntityId,
        blockingPlayer: EntityId,
        projected: ProjectedState
    ): Boolean {
        val container = state.getEntity(blockerId) ?: return false
        if (container.has<FaceDownComponent>()) return false
        val cardComponent = container.get<CardComponent>() ?: return false
        val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: return false

        val restriction = cardDef.staticAbilities
            .filterIsInstance<CantBlockUnless>()
            .firstOrNull { it.filter.scope is com.wingedsheep.sdk.scripting.filters.unified.Scope.Self } ?: return false

        val attackers = state.entities.filter { (_, c) -> c.has<AttackingComponent>() }
        if (attackers.isEmpty()) return false

        val anyAttacker = attackers.keys.first()
        val attackingPlayer = projected.getController(anyAttacker) ?: return false

        val effectContext = EffectContext(
            sourceId = blockerId,
            controllerId = blockingPlayer,
        )
        return !conditionEvaluator.evaluate(state, restriction.condition, effectContext)
    }

    // =========================================================================
    // Block Taxes
    // =========================================================================

    private fun pauseForBlockTaxConfirmation(
        state: GameState,
        blockingPlayer: EntityId,
        blockers: Map<EntityId, List<EntityId>>,
        taxByPayer: Map<EntityId, Int>,
    ): ExecutionResult {
        val manaSolver = com.wingedsheep.engine.mechanics.mana.ManaSolver(cardRegistry)
        val atomicTeamPayment = state.sharedTurnTeam(blockingPlayer).size > 1
        val payerOrder = state.sharedTurnTeam(blockingPlayer).filter(taxByPayer::containsKey)
        val payerPlans = payerOrder.map { payerId ->
            val totalTax = taxByPayer.getValue(payerId)
            val manaCost = com.wingedsheep.sdk.core.ManaCost(
                List(totalTax) { com.wingedsheep.sdk.core.ManaSymbol.generic(1) }
            )
            // Only the new atomic shared-team path needs a side-effect-free source subset:
            // teammates may still decline later. Keep the established one-player tax payment
            // surface intact, including its solver support for complex mana abilities.
            val solverSources = manaSolver.findAvailableManaSources(state, payerId)
            val sources = solverSources.let { candidates ->
                if (!atomicTeamPayment) candidates else candidates.filter { source ->
                    !source.requiresSacrifice &&
                        source.tapPermanentsSubCost == null &&
                        !source.hasPainCost &&
                        source.painAmount == 0 &&
                        source.manaAmount > 0 &&
                        // Atomic team payment has no nested colour-choice continuation.  Admit
                        // only deterministic fixed-output sources; surplus stays in this
                        // controller's own pool after their tax is paid.
                        ((source.producesColors.size == 1 && !source.producesColorless) ||
                            (source.producesColors.isEmpty() && source.producesColorless)) &&
                        source.bonusManaPerTap == 0 &&
                        source.bonusManaColorlessPerTap == 0 &&
                        source.restriction == null &&
                        source.colorRestrictions.isEmpty() &&
                        source.colorActivationManaCost.isEmpty() &&
                        source.colorPainCost.isEmpty() &&
                        source.colorlessPainCost == 0 &&
                        source.colorsRequiringSacrifice.isEmpty()
                }.map { source ->
                    // The atomic source picker has no ability-branch payload.  For a mixed
                    // source such as Crystal Vein, expose only its non-sacrifice branch rather
                    // than granting the larger sacrifice branch without paying that cost.
                    source.copy(manaAmount = source.nonSacrificeManaAmount)
                }
            }
            val sourceOptions = sources.map { source ->
                com.wingedsheep.engine.core.ManaSourceOption(
                    entityId = source.entityId,
                    name = source.name,
                    producesColors = source.producesColors,
                    producesColorless = source.producesColorless,
                    manaAmount = source.manaAmount,
                    requiresSacrifice = source.requiresSacrifice,
                    requiresTappingAnotherPermanent = source.tapPermanentsSubCost != null,
                )
            }
            // A shared-team declaration cannot mutate any payer's state until every teammate has
            // accepted.  Unlike the ordinary aggregate-source picker, the atomic menu must name
            // one concrete printed mana ability: a Crystal Vein selection therefore says whether
            // it is `{T}: {C}` or `{T}, sacrifice: {C}{C}`.
            val pool = state.getEntity(payerId)?.get<ManaPoolComponent>()?.let {
                ManaPool(it.white, it.blue, it.black, it.red, it.green, it.colorless)
            } ?: ManaPool()
            val remainingCost = pool.payPartial(manaCost).remainingCost
            // Even if this payer's floating mana already covers the generic tax, they may
            // deliberately activate a bounded Signet branch: its pre-existing `{1}` pays the
            // activation cost and one of its new outputs pays the tax. Do not erase that legal
            // exact branch merely because the ordinary solver would need no source.
            val atomicOptions = if (atomicTeamPayment) {
                atomicFixedManaAbilityOptions(state, payerId, solverSources).filter { option ->
                    // The bounded Signet branch cannot use mana it itself produces to pay its
                    // activation cost. It is offered only when this payer already has `{1}`.
                    option.activationManaCost?.let { pool.pay(it) != null } ?: true
                }
            } else emptyList()
            val solution = if (remainingCost.isEmpty()) null else {
                manaSolver.solve(
                    state = state,
                    playerId = payerId,
                    cost = remainingCost,
                    precomputedSources = sources,
                )
            }
            val atomicAutoPaySelections = atomicAutoPaySuggestion(pool, manaCost, atomicOptions)
            com.wingedsheep.engine.core.BlockTaxPayerPlan(
                payerId = payerId,
                manaCost = manaCost,
                availableSources = sourceOptions,
                autoPaySuggestion = solution?.sources?.map { it.entityId } ?: emptyList(),
                atomicManaAbilityOptions = atomicOptions,
                // Keep the old ref payload populated for saved fixed-branch frames; new
                // color-qualified selections are carried separately.
                atomicAutoPaySuggestion = atomicAutoPaySelections.map { it.ref },
                atomicAutoPaySelections = atomicAutoPaySelections,
            )
        }
        val firstPlan = payerPlans.first()
        val decisionId = java.util.UUID.randomUUID().toString()
        val decision = if (atomicTeamPayment) atomicBlockTaxDecision(decisionId, firstPlan) else
            com.wingedsheep.engine.core.SelectManaSourcesDecision(
                id = decisionId,
                playerId = firstPlan.payerId,
                prompt = "Pay {${firstPlan.manaCost.cmc}} to block with your declared creatures",
                context = com.wingedsheep.engine.core.DecisionContext(
                    sourceId = null, sourceName = "Block tax", phase = com.wingedsheep.engine.core.DecisionPhase.COMBAT,
                ),
                availableSources = firstPlan.availableSources,
                requiredCost = firstPlan.manaCost.toString(),
                autoPaySuggestion = firstPlan.autoPaySuggestion,
                canDecline = true,
            )
        val continuation = com.wingedsheep.engine.core.BlockTaxManaSelectionContinuation(
            decisionId = decisionId,
            blockingPlayer = blockingPlayer,
            blockers = blockers,
            payerPlans = payerPlans,
            isAtomicTeamPayment = atomicTeamPayment,
        )
        return ExecutionResult.paused(
            state.withPendingDecision(decision).pushContinuation(continuation),
            decision,
        )
    }

    private fun atomicBlockTaxDecision(
        decisionId: String,
        plan: BlockTaxPayerPlan,
    ) = SelectAtomicBlockTaxManaAbilitiesDecision(
        id = decisionId,
        playerId = plan.payerId,
        prompt = "Pay {${plan.manaCost.cmc}} to block with your declared creatures",
        context = DecisionContext(sourceId = null, sourceName = "Block tax", phase = DecisionPhase.COMBAT),
        availableOptions = plan.atomicManaAbilityOptions,
        requiredCost = plan.manaCost.toString(),
        autoPaySuggestion = plan.atomicAutoPaySuggestion,
        autoPaySelections = plan.atomicAutoPaySelections,
    )

    /**
     * The narrow, side-effect-free branch vocabulary accepted by the team transaction.  Ordinary
     * mana payment remains deliberately broader.  We only expose targetless printed mana abilities
     * with a fixed single-color/colorless output and exactly `{T}` or `{T}, Sacrifice this` costs.
     */
    private fun atomicFixedManaAbilityOptions(
        state: GameState,
        payerId: EntityId,
        solverSources: List<com.wingedsheep.engine.mechanics.mana.ManaSource>,
    ): List<AtomicBlockTaxManaAbilityOption> {
        val projected = state.projectedState
        val solverById = solverSources.associateBy { it.entityId }
        return projected.getBattlefieldControlledBy(payerId).flatMap { sourceId ->
            val container = state.getEntity(sourceId) ?: return@flatMap emptyList()
            if (container.has<TappedComponent>() || projected.hasLostAllAbilities(sourceId)) return@flatMap emptyList()
            val card = container.get<CardComponent>() ?: return@flatMap emptyList()
            val definition = cardRegistry.getCard(card.cardDefinitionId) ?: return@flatMap emptyList()
            val source = solverById[sourceId] ?: return@flatMap emptyList()
            val printedOptions = definition.script.activatedAbilities.mapIndexedNotNull { index, ability ->
                if (!ability.isManaAbility || ability.targetRequirements.isNotEmpty() || ability.restrictions.isNotEmpty()) return@mapIndexedNotNull null
                val signetActivationManaCost = atomicTwoColourSignetActivationManaCost(ability.cost)
                val secondaryTap = atomicSecondaryCreatureTapCost(ability.cost)
                val sacrifice = when (ability.cost) {
                    AbilityCost.Tap -> false
                    is AbilityCost.Composite -> {
                        val costs = (ability.cost as AbilityCost.Composite).costs
                        when {
                            costs.size == 2 && costs.contains(AbilityCost.Tap) && costs.contains(AbilityCost.SacrificeSelf) -> true
                            secondaryTap -> false
                            signetActivationManaCost != null -> false
                            else -> return@mapIndexedNotNull null
                        }
                    }
                    else -> return@mapIndexedNotNull null
                }
                val fixed = when (val effect = ability.effect) {
                    is AddManaEffect -> {
                        val amount = (effect.amount as? DynamicAmount.Fixed)?.amount ?: return@mapIndexedNotNull null
                        AtomicManaOutput(setOf(effect.color), false, amount)
                    }
                    is AddColorlessManaEffect -> {
                        val amount = (effect.amount as? DynamicAmount.Fixed)?.amount ?: return@mapIndexedNotNull null
                        AtomicManaOutput(emptySet(), true, amount)
                    }
                    // This intentionally recognizes only the direct, targetless Gilded Lotus
                    // shape. Dynamic/restricted/granted/rider-bearing and secondary-cost
                    // abilities keep using the ordinary payment path until they have their own
                    // explicit atomic transaction vocabulary.
                    is AddManaOfChoiceEffect -> {
                        if (effect.colorSet !is ManaColorSet.AnyColor || effect.restriction != null ||
                            effect.riders.isNotEmpty() || effect.recipient != com.wingedsheep.sdk.scripting.targets.EffectTarget.Controller
                        ) return@mapIndexedNotNull null
                        val amount = (effect.amount as? DynamicAmount.Fixed)?.amount ?: return@mapIndexedNotNull null
                        AtomicManaOutput(emptySet(), false, amount, com.wingedsheep.sdk.core.Color.entries.toSet())
                    }
                    // Deliberately not a general CompositeEffect implementation. This is the
                    // exact two-colour Signet shape that the atomic 2HG transaction can replay:
                    // `{1}, {T}: Add {A}{B}`. Other activation costs and composite outputs
                    // remain outside this vocabulary until they get an explicit design.
                    is CompositeEffect -> atomicTwoColourSignetOutput(effect, signetActivationManaCost)
                        ?: atomicPainManaOutput(effect, ability.cost == AbilityCost.Tap)
                        ?: return@mapIndexedNotNull null
                    else -> return@mapIndexedNotNull null
                }
                // The solver has already applied source-tap replacement effects (for example
                // Virtue of Strength).  Derive that factor from the ordinary non-sacrifice branch
                // so this branch-specific surface cannot silently drop a multiplier.
                val normalPrinted = definition.script.activatedAbilities.mapNotNull { other ->
                    val amount = when (val effect = other.effect) {
                        is AddManaEffect -> (effect.amount as? DynamicAmount.Fixed)?.amount
                        is AddColorlessManaEffect -> (effect.amount as? DynamicAmount.Fixed)?.amount
                        else -> null
                    }
                    if (other.cost == AbilityCost.Tap) amount else null
                }.maxOrNull() ?: fixed.amount
                val multiplier = if (normalPrinted > 0 && source.nonSacrificeManaAmount % normalPrinted == 0)
                    source.nonSacrificeManaAmount / normalPrinted else 1
                val secondaryTargets = if (secondaryTap) {
                    projected.getBattlefieldControlledBy(payerId)
                        .asSequence()
                        .filter { it != sourceId && projected.isCreature(it) }
                        .filter { candidate -> state.getEntity(candidate)?.has<TappedComponent>() != true }
                        .mapNotNull { candidate ->
                            state.getEntity(candidate)?.get<CardComponent>()?.name?.let { name ->
                                AtomicBlockTaxSecondaryTapTarget(candidate, name)
                            }
                        }
                        .toList()
                } else emptyList()
                if (secondaryTap && secondaryTargets.isEmpty()) return@mapIndexedNotNull null
                AtomicBlockTaxManaAbilityOption(
                    ref = AtomicBlockTaxManaAbilityRef(sourceId, index),
                    sourceName = card.name,
                    description = ability.description,
                    producesColors = fixed.colors,
                    producesColorless = fixed.colorless,
                    manaAmount = fixed.amount * multiplier,
                    requiresSacrificeSelf = sacrifice,
                    colorChoices = fixed.colorChoices,
                    activationManaCost = signetActivationManaCost,
                    fixedProducedMana = fixed.fixedProducedMana,
                    taxPaymentColorChoices = fixed.taxPaymentColorChoices,
                    secondaryTapTargets = secondaryTargets,
                    hasImmediateSelfDamage = fixed.hasImmediateSelfDamage,
                )
            }
            // Basic-land subtype mana abilities are intrinsic rather than printed in a card's
            // script. Negative indexes reserve a replay-stable namespace distinct from every
            // printed list index; the order comes from the shared intrinsic helper.
            val intrinsicOptions = IntrinsicManaAbilities.forEntity(state, projected, sourceId)
                .mapIndexed { intrinsicIndex, ability ->
                    val effect = ability.effect as AddManaEffect
                    val amount = (effect.amount as? DynamicAmount.Fixed)?.amount ?: 1
                    val multiplier = if (amount > 0 && source.nonSacrificeManaAmount % amount == 0)
                        source.nonSacrificeManaAmount / amount else 1
                    AtomicBlockTaxManaAbilityOption(
                        ref = AtomicBlockTaxManaAbilityRef(sourceId, -1 - intrinsicIndex),
                        sourceName = card.name,
                        description = ability.description,
                        producesColors = setOf(effect.color),
                        producesColorless = false,
                        manaAmount = amount * multiplier,
                        requiresSacrificeSelf = false,
                    )
                }
            printedOptions + intrinsicOptions
        }
    }

    /** Exact `{T}, tap an untapped creature you control` branch used by Springleaf Drum. */
    private fun atomicSecondaryCreatureTapCost(cost: AbilityCost): Boolean {
        val parts = (cost as? AbilityCost.Composite)?.costs ?: return false
        if (parts.size != 2 || AbilityCost.Tap !in parts) return false
        val tapOther = parts.filterIsInstance<AbilityCost.Atom>()
            .mapNotNull { it.atom as? CostAtom.TapPermanents }
            .singleOrNull() ?: return false
        return tapOther.count == 1 && tapOther.filter == GameObjectFilter.Creature && !tapOther.excludeSelf
    }

    private data class AtomicManaOutput(
        val colors: Set<com.wingedsheep.sdk.core.Color>,
        val colorless: Boolean,
        val amount: Int,
        val colorChoices: Set<com.wingedsheep.sdk.core.Color> = emptySet(),
        val fixedProducedMana: Map<com.wingedsheep.sdk.core.Color, Int> = emptyMap(),
        val taxPaymentColorChoices: Set<com.wingedsheep.sdk.core.Color> = emptySet(),
        val hasImmediateSelfDamage: Boolean = false,
    )

    /** Exact parser for the bounded two-colour Signet activation-cost branch, not a cost-shape generalizer. */
    private fun atomicTwoColourSignetActivationManaCost(cost: AbilityCost): ManaCost? {
        val parts = (cost as? AbilityCost.Composite)?.costs ?: return null
        if (parts.size != 2 || AbilityCost.Tap !in parts) return null
        val mana = parts.filterIsInstance<AbilityCost.Atom>()
            .mapNotNull { (it.atom as? CostAtom.Mana)?.cost }
            .singleOrNull() ?: return null
        return mana.takeIf { it.cmc == 1 && it.genericAmount == 1 && it.colors.isEmpty() && it.colorlessAmount == 0 }
    }

    /** Exact `{A}{B}` fixed output paired with [atomicTwoColourSignetActivationManaCost]. */
    private fun atomicTwoColourSignetOutput(
        effect: CompositeEffect,
        activationManaCost: ManaCost?,
    ): AtomicManaOutput? {
        if (activationManaCost == null || effect.effects.size != 2 || effect.stopOnError || effect.descriptionOverride != null) return null
        val produced = effect.effects.mapNotNull { part ->
            val add = part as? AddManaEffect ?: return null
            val amount = (add.amount as? DynamicAmount.Fixed)?.amount ?: return null
            add.color to amount
        }.toMap()
        // A guild Signet has exactly two distinct coloured pips. This syntactic check keeps the
        // transaction narrow rather than admitting arbitrary composite mana abilities.
        if (produced.size != 2 || produced.values.any { it != 1 }) return null
        return AtomicManaOutput(
            colors = produced.keys,
            colorless = false,
            amount = 2,
            fixedProducedMana = produced,
            taxPaymentColorChoices = produced.keys,
        )
    }

    /**
     * Exact pain-land rider: `{T}: Add {C}. This land deals 1 damage to you.` in its coloured
     * branch form. The damage is executed only after all team payment intents have been accepted.
     * Broader side-effecting mana abilities remain outside the atomic vocabulary.
     */
    private fun atomicPainManaOutput(effect: CompositeEffect, hasTapOnlyCost: Boolean): AtomicManaOutput? {
        if (!hasTapOnlyCost || effect.effects.size != 2 || effect.stopOnError || effect.descriptionOverride != null) return null
        val add = effect.effects[0] as? AddManaEffect ?: return null
        val amount = (add.amount as? DynamicAmount.Fixed)?.amount ?: return null
        val damage = effect.effects[1] as? DealDamageEffect ?: return null
        val selfTarget = damage.target == EffectTarget.Controller ||
            (damage.target as? EffectTarget.PlayerRef)?.player == Player.You
        if (!selfTarget || damage.damageSource != null || damage.cantBePrevented || damage.excessToController ||
            (damage.amount as? DynamicAmount.Fixed)?.amount != 1
        ) return null
        return AtomicManaOutput(
            colors = setOf(add.color),
            colorless = false,
            amount = amount,
            hasImmediateSelfDamage = true,
        )
    }

    private fun atomicAutoPaySuggestion(
        pool: ManaPool,
        manaCost: ManaCost,
        options: List<AtomicBlockTaxManaAbilityOption>,
    ): List<AtomicBlockTaxManaAbilitySelection> {
        var remaining = pool.payPartial(manaCost).remainingCost.cmc
        if (remaining <= 0) return emptyList()
        // A permanent may expose several exact branches (Crystal Vein). An auto-pay plan may
        // activate at most one branch from each source; choose the strongest deterministic
        // branch first so a valid sacrifice branch is not shadowed by its weaker tap-only branch.
        return options.groupBy { it.ref.sourceId }.values.map { branches ->
            branches.sortedWith(
                compareByDescending<AtomicBlockTaxManaAbilityOption> { it.manaAmount }
                    .thenBy { it.requiresSacrificeSelf }
                    .thenBy { it.ref.printedManaAbilityIndex }
            ).first()
        }.sortedWith(compareBy<AtomicBlockTaxManaAbilityOption> { it.requiresSacrificeSelf }.thenByDescending { it.manaAmount })
            .takeWhile { option ->
                if (remaining <= 0) false else { remaining -= option.manaAmount; true }
            }
            .map { option ->
                AtomicBlockTaxManaAbilitySelection(
                    ref = option.ref,
                    // Generic block tax does not constrain the colour, but replay does: choose a
                    // deterministic canonical colour for an automatic any-one-colour branch.
                    chosenColor = option.colorChoices.minByOrNull { it.name },
                    taxPaymentColor = option.taxPaymentColorChoices.minByOrNull { it.name },
                )
            }
    }

    /**
     * Calculate per-creature tax from AttackBlockTaxPerCreatureType floating effects.
     */
    private fun calculatePerCreatureTax(
        state: GameState,
        creatureIds: Set<EntityId>,
        projected: ProjectedState
    ): Int {
        var totalTax = 0
        for (creatureId in creatureIds) {
            for (floatingEffect in state.floatingEffects) {
                val mod = floatingEffect.effect.modification
                if (mod !is SerializableModification.AttackBlockTaxPerCreatureType) continue
                if (creatureId !in floatingEffect.effect.affectedEntities) continue

                val creatureTypeCount = state.getBattlefield().count { entityId ->
                    projected.isCreature(entityId) && projected.hasSubtype(entityId, mod.creatureType)
                }
                val costPerCreature = ManaCost.parse(mod.manaCostPer).cmc
                totalTax += costPerCreature * creatureTypeCount
            }
        }
        return totalTax
    }

    /**
     * Calculate the generic-mana block tax from [BlockTax] static abilities (Archangel of Tithes —
     * "creatures can't block unless their controller pays {1} for each of those creatures").
     *
     * Unlike [AttackTax], this is a global restriction: every permanent on the battlefield with a
     * [BlockTax] ability (whose optional condition holds, e.g. "as long as this creature is
     * attacking") taxes each declared blocker by its per-blocker amount. Multiple sources stack.
     */
    private fun calculateBlockTax(
        state: GameState,
        blockerIds: Set<EntityId>,
        projected: ProjectedState
    ): Int {
        if (blockerIds.isEmpty()) return 0
        var totalTax = 0
        for (entityId in state.getBattlefield()) {
            val container = state.getEntity(entityId) ?: continue
            val cardComponent = container.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: continue
            for (ability in cardDef.staticAbilities) {
                if (ability !is BlockTax) continue
                val controllerId = projected.getController(entityId) ?: continue
                val ctx = EffectContext(
                    sourceId = entityId,
                    controllerId = controllerId,
                )
                val condition = ability.condition
                if (condition != null && !conditionEvaluator.evaluate(state, condition, ctx)) {
                    continue
                }
                val taxPerBlocker = maxOf(0, dynamicAmountEvaluator.evaluate(state, ability.amountPerBlocker, ctx, projected))
                totalTax += taxPerBlocker * blockerIds.size
            }
        }
        return totalTax
    }
}
