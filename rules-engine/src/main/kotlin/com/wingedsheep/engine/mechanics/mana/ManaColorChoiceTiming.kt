package com.wingedsheep.engine.mechanics.mana

import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * When the color of an "any color" mana ability is chosen.
 *
 * Ordinarily the activating player names the color as part of activating the ability (the client's
 * `manaColorChoice`, read by `ActivateAbilityHandler`). That is only right while the activating
 * player is the one who chooses. "Choose a player. That player adds one mana of any color **they**
 * choose" (Spectral Searchlight) hands the choice to a player who isn't known until the ability
 * resolves, so such an ability must never accept an activation-time color: the enumerator doesn't
 * ask for one, and the handler discards one a client sends anyway (a `GameAction` is
 * client-supplied — it can't be trusted to leave the field empty).
 */
object ManaColorChoiceTiming {

    /** True when [effect]'s mana color is chosen at resolution by someone other than the activator. */
    fun chosenByAnotherPlayerAtResolution(effect: Effect): Boolean = when (effect) {
        is AddManaOfChoiceEffect ->
            effect.colorChosenByRecipient && effect.recipient != EffectTarget.Controller
        is CompositeEffect -> effect.effects.any { chosenByAnotherPlayerAtResolution(it) }
        else -> false
    }
}
