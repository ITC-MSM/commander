package com.wingedsheep.engine.state.components.identity

import com.wingedsheep.engine.state.Component
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import kotlinx.serialization.Serializable

/** Activated abilities granted dynamically by a permanent emblem. */
@Serializable
data class EmblemActivatedAbilityComponent(
    val filter: GroupFilter,
    val abilities: List<ActivatedAbility>,
) : Component
