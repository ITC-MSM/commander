package com.wingedsheep.engine.event

import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggeredAbility
import kotlinx.serialization.Serializable

/**
 * A triggered ability that has been granted to an entity temporarily.
 *
 * Used for effects like Commando Raid that grant triggered abilities
 * until end of turn. Stored in GameState.grantedTriggeredAbilities
 * and checked by TriggerDetector when looking up abilities for entities.
 *
 * @property entityId The entity that has the granted ability
 * @property ability The triggered ability that was granted
 * @property duration How long the grant lasts
 * @property sourceId The permanent (or spell) whose effect made the grant, when there is one.
 *   Only the *source-keyed* "for as long as …" durations read it — `WhileSourceOnBattlefield`,
 *   `WhileSourceTapped`, `WhileSourceAttachedToAffected` — which
 *   [com.wingedsheep.engine.mechanics.sba.permanent.EndedDurationExpiryCheck] uses to end the
 *   grant. Null for a grant made by a spell that is already gone (Makeshift Mannequin) and for
 *   the token-creation grants, whose duration is `Permanent`.
 */
@Serializable
data class GrantedTriggeredAbility(
    val entityId: EntityId,
    val ability: TriggeredAbility,
    val duration: Duration,
    val sourceId: EntityId? = null
)
