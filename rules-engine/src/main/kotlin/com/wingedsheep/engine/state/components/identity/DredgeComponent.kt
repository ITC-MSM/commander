package com.wingedsheep.engine.state.components.identity

import com.wingedsheep.engine.state.Component
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.scripting.ReplaceDrawWithEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Intrinsic dredge abilities, retained across zones and active only in the graveyard. */
@Serializable
data class DredgeComponent(val amounts: List<Int>) : Component {
    // Compile each immutable recipe once, rather than allocate effect trees on every draw check.
    // Derived data is rebuilt from amounts when a saved game is deserialized.
    @Transient
    val replacements: List<ReplaceDrawWithEffect> = amounts.map { amount ->
        ReplaceDrawWithEffect(
            replacementEffect = Patterns.Library.mill(amount) then Effects.ReturnToHand(EffectTarget.Self),
            optional = true
        )
    }
}
