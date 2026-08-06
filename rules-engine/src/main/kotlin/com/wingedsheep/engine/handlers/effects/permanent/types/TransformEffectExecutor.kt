package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.TransformedEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneEntryOptions
import com.wingedsheep.engine.handlers.effects.ZoneTransitionResult
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.layers.StaticAbilityHandler
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.ReplacementEffectSourceComponent
import com.wingedsheep.engine.mechanics.layers.ContinuousEffectSourceComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.SelfZoneRedirectComponent
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import kotlin.reflect.KClass

/**
 * Executor for [TransformEffect] (CR 701.27).
 *
 * Swaps the target's [CardComponent] to the opposite face of its [DoubleFacedComponent].
 * Counters, damage, attachments, controller, and timestamp all persist — only the identity
 * characteristics (name, type line, P/T, keywords, colors, oracle text, abilities) change.
 *
 * The card's static abilities' [ContinuousEffectSourceComponent] and
 * [ReplacementEffectSourceComponent] are rebuilt from the new face's definition so that
 * layer projection picks up the new face's static abilities immediately.
 *
 * Emits a [TransformedEvent] so triggered abilities keyed on "when this transforms"
 * fire through the standard trigger pipeline.
 */
class TransformEffectExecutor(
    private val cardRegistry: CardRegistry
) : EffectExecutor<TransformEffect> {

    override val effectType: KClass<TransformEffect> = TransformEffect::class

    override fun execute(
        state: GameState,
        effect: TransformEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target)
            ?: return EffectResult.error(state, "No valid target for transform")

        if (state.getEntity(targetId) == null) {
            return EffectResult.error(state, "Target entity not found")
        }

        // CR 702.145b#3 / 702.145e#2 — a permanent with daybound or nightbound "can't transform except
        // due to its daybound/nightbound ability." Those keyword-driven transforms don't come through
        // this executor (they route through DayNightService.flipDfcInPlace on a day/night change), so
        // any TransformEffect reaching a daybound/nightbound permanent is a disallowed "other" cause
        // and does nothing.
        val projected = state.projectedState
        if (projected.hasKeyword(targetId, Keyword.DAYBOUND) ||
            projected.hasKeyword(targetId, Keyword.NIGHTBOUND)
        ) {
            return EffectResult.success(state)
        }

        // A non-DFC target is a silent no-op (CR 701.27b — "if a permanent that isn't a
        // transforming double-faced permanent would transform, nothing happens").
        val (newState, event) = flipDfcInPlace(state, cardRegistry, targetId)
            ?: return EffectResult.success(state)

        return EffectResult.success(newState, listOf(event))
    }

}

/**
 * Flip the double-faced permanent [entityId] to its opposite face **in place** on the battlefield
 * (CR 701.27a) and return the new state paired with the [TransformedEvent] the flip emits, or `null`
 * when [entityId] is not a double-faced permanent (the caller decides whether that's a no-op or an
 * error). The entity id is stable; counters, damage, attachments, controller, and timestamp persist
 * — only the identity characteristics change, and the new face's static/replacement effects are
 * re-registered so layer projection picks them up on the next projection.
 *
 * The single shared implementation behind both [TransformEffectExecutor] (the [TransformEffect]
 * one-shot) and [com.wingedsheep.engine.mechanics.daynight.DayNightService] (the immediate
 * daybound/nightbound transforms of CR 702.145c/f). Routing both through here guarantees a day/night
 * transform is byte-identical to any other transform and emits the same [TransformedEvent], so
 * "whenever this transforms" triggers (e.g. Wildsong Howler) fire identically however the flip was
 * caused — and it makes this the one place a "can't transform" restriction has to be honored.
 */
internal fun flipDfcInPlace(
    state: GameState,
    cardRegistry: CardRegistry,
    entityId: EntityId
): Pair<GameState, TransformedEvent>? {
    // CR 701.27b — a permanent that can't transform simply doesn't (Bound by Moonsilver). Checked
    // here rather than in the callers so it covers every cause of a transform, including the
    // daybound/nightbound day-change flips that bypass TransformEffectExecutor entirely.
    if (state.projectedState.hasKeyword(entityId, AbilityFlag.CANT_TRANSFORM)) return null

    val container = state.getEntity(entityId) ?: return null
    val dfc = container.get<DoubleFacedComponent>() ?: return null
    val currentCard = container.get<CardComponent>() ?: return null

    // Rule 701.27a: transforming a DFC flips to the opposite face.
    val nextFace = when (dfc.currentFace) {
        DoubleFacedComponent.Face.FRONT -> DoubleFacedComponent.Face.BACK
        DoubleFacedComponent.Face.BACK -> DoubleFacedComponent.Face.FRONT
    }
    val intoBackFace = nextFace == DoubleFacedComponent.Face.BACK

    val nextDefinitionId = when (nextFace) {
        DoubleFacedComponent.Face.FRONT -> dfc.frontCardDefinitionId
        DoubleFacedComponent.Face.BACK -> dfc.backCardDefinitionId
    }
    val nextCardDef = cardRegistry.getCard(nextDefinitionId) ?: return null

    val swappedCard = buildCardComponentForDfcFace(currentCard, nextCardDef)
    // A DFC on the battlefield always has a controller; fall back to owner, and treat a truly
    // owner-less object as un-flippable (null → the caller's no-op contract) rather than fabricate an id.
    val controllerId = container.get<ControllerComponent>()?.playerId ?: currentCard.ownerId ?: return null

    // Rule 712.8a: save the front face card so ZoneTransitionService can restore it
    // without a registry lookup when the DFC leaves the battlefield on its back face.
    val updatedDfc = if (intoBackFace) {
        dfc.copy(currentFace = nextFace, frontFaceCard = currentCard)
    } else {
        dfc.copy(currentFace = nextFace, frontFaceCard = null)
    }

    val staticAbilityHandler = StaticAbilityHandler(cardRegistry)
    val newState = state.updateEntity(entityId) { c ->
        var updated = c
            .with(swappedCard)
            .with(updatedDfc)
            // Strip stale static-ability effect components so the layer projector stops
            // applying the old face's static abilities on the very next projection.
            .without<ContinuousEffectSourceComponent>()
            .without<ReplacementEffectSourceComponent>()

        // Re-register the new face's static and replacement effects.
        updated = staticAbilityHandler.addContinuousEffectComponent(updated, nextCardDef)
        updated = staticAbilityHandler.addReplacementEffectComponent(updated, nextCardDef)
        updated = withDfcFaceSelfRedirects(updated, nextCardDef)
        updated
    }

    return newState to TransformedEvent(
        entityId = entityId,
        intoBackFace = intoBackFace,
        newFaceName = nextCardDef.name,
        controllerId = controllerId
    )
}

/**
 * Re-derive the entity's card-intrinsic "would be put into [zone] from anywhere → redirect instead"
 * self-replacements ([SelfZoneRedirectComponent]) from [face].
 *
 * That component is normally built once, at entity creation, from the printed front face — so
 * without this a face swap would leave the wrong face's redirects in place. It matters for the
 * disturb cycle, whose back faces each print "If ~ would be put into a graveyard from anywhere,
 * exile it instead": the clause has to start applying the moment the card becomes a back-face
 * object (CR 614.12 — it functions in every zone, so a countered disturb spell is exiled rather
 * than put into the graveyard), and stop applying when Rule 712.8a turns the card back over.
 *
 * Called from every face swap: [flipDfcInPlace], [returnDfcFace], the disturb cast in
 * `StackResolver.castSpell`, and the 712.8a restore in `ZoneTransitionService`.
 */
internal fun withDfcFaceSelfRedirects(
    container: ComponentContainer,
    face: CardDefinition
): ComponentContainer {
    val redirects = face.script.replacementEffects
        .filterIsInstance<RedirectZoneChange>()
        .filter { it.selfOnly }
    return if (redirects.isEmpty()) {
        container.without<SelfZoneRedirectComponent>()
    } else {
        container.with(SelfZoneRedirectComponent(redirects))
    }
}

/**
 * Build a fresh [CardComponent] for the given DFC face while preserving the permanent's
 * owner identity and inheriting the prior face's `imageUri` when the new face doesn't
 * declare its own. Shared by [TransformEffectExecutor] (CR 701.27 transform on the
 * battlefield) and [ReturnSelfFromExileTransformedExecutor] (CR 702.167 Craft return).
 */
internal fun buildCardComponentForDfcFace(
    current: CardComponent,
    face: CardDefinition
): CardComponent = CardComponent(
    cardDefinitionId = face.name,
    name = face.name,
    manaCost = face.manaCost,
    typeLine = face.typeLine,
    oracleText = face.oracleText,
    baseStats = face.creatureStats,
    baseKeywords = face.keywords,
    baseFlags = face.flags,
    colors = face.colors,
    ownerId = current.ownerId,
    spellEffect = face.spellEffect,
    imageUri = face.metadata.imageUri ?: current.imageUri,
    // Carry the destination face's precomputed ability flags so predicates like
    // CardPredicate.HasActivatedAbility / HasNonManaActivatedAbility see the face that's actually up
    // (otherwise a transformed permanent silently reports the default `false`).
    hasNonManaActivatedAbility = face.hasNonManaActivatedAbility,
    hasActivatedAbility = face.hasActivatedAbility,
)

/**
 * Flip a double-faced entity that is currently in a non-battlefield zone (exile or graveyard) to
 * [destinationFace] and return it to the battlefield as a new object under its owner's control.
 *
 * The face swap is applied while the entity is still in its source zone so the → BATTLEFIELD move
 * registers the destination face's static abilities and (for a Saga face) the lore-counter entry
 * setup cleanly. Per Rule 712.8a the front face's [CardComponent] is stashed on the
 * [DoubleFacedComponent] when going to the back face, so the restore-on-leave path can swap back
 * without a registry lookup.
 *
 * Shared by [ReturnSelfFromExileTransformedExecutor] (CR 702.167a Craft return — always to the
 * back face), [ExileAndReturnTransformedExecutor] (FIN Dominant / eikon — either direction), and
 * [ReturnSelfFromZoneTransformedExecutor] (graveyard return — LCI god cycle). The caller is
 * responsible for the entity already being in the zone it is to be returned from.
 */
internal fun returnDfcFace(
    state: GameState,
    cardRegistry: CardRegistry,
    entityId: EntityId,
    destinationFace: DoubleFacedComponent.Face,
    tapped: Boolean = false
): ZoneTransitionResult {
    val container = state.getEntity(entityId)
        ?: return ZoneTransitionResult(state, emptyList())
    val dfc = container.get<DoubleFacedComponent>()
        ?: return ZoneTransitionResult(state, emptyList())
    val currentCard = container.get<CardComponent>()
        ?: return ZoneTransitionResult(state, emptyList())
    val ownerId = container.get<OwnerComponent>()?.playerId ?: currentCard.ownerId
        ?: return ZoneTransitionResult(state, emptyList())

    val destinationDefinitionId = when (destinationFace) {
        DoubleFacedComponent.Face.FRONT -> dfc.frontCardDefinitionId
        DoubleFacedComponent.Face.BACK -> dfc.backCardDefinitionId
    }
    val destinationDef = cardRegistry.getCard(destinationDefinitionId)
        ?: return ZoneTransitionResult(state, emptyList())

    val destinationCard = buildCardComponentForDfcFace(currentCard, destinationDef)
    val updatedDfc = when (destinationFace) {
        // currentCard is the front face here (the entity reverts to its front face on leaving the
        // battlefield, Rule 712.8a) — stash it so the back face can restore it on its next exit.
        DoubleFacedComponent.Face.BACK -> dfc.copy(currentFace = destinationFace, frontFaceCard = currentCard)
        DoubleFacedComponent.Face.FRONT -> dfc.copy(currentFace = destinationFace, frontFaceCard = null)
    }

    val prepared = state.updateEntity(entityId) { c ->
        withDfcFaceSelfRedirects(c.with(destinationCard).with(updatedDfc), destinationDef)
    }
    return ZoneTransitionService.moveToZone(
        prepared,
        entityId,
        Zone.BATTLEFIELD,
        options = ZoneEntryOptions(controllerId = ownerId, tapped = tapped)
    )
}
