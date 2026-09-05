package com.wingedsheep.engine.state.components.stack

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.AbilityIdentity

/**
 * Transitional trigger-path derivation that pairs [abilityId] with [sourceId]'s current card
 * definition.
 *
 * This says only how the key was derived; it does not prove that the trigger belongs to that card
 * definition. [com.wingedsheep.engine.event.TriggerAbilityResolver] can also return granted and
 * synthesized triggers, so those branches need typed provenance before trigger identity is fully
 * authoritative. Activated abilities do not use this helper: their lookup records definition
 * ownership explicitly at activation time.
 *
 * Returns `null` when [sourceId] has no [CardComponent].
 */
fun GameState.triggerIdentityFromCurrentCardDefinition(
    sourceId: EntityId,
    abilityId: AbilityId,
): AbilityIdentity? =
    getEntity(sourceId)?.get<CardComponent>()?.cardDefinitionId
        ?.let { AbilityIdentity(it, abilityId) }
