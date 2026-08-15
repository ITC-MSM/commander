package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Divergent Equation
 * {X}{X}{U}
 * Instant
 *
 * Return up to X target instant and/or sorcery cards from your graveyard to your hand.
 * Exile Divergent Equation.
 *
 * X-clamped graveyard targeting (Restock + Distorting Wake patterns): [TargetObject.dynamicMaxCount]
 * = [DynamicAmount.XValue] makes the targeting overlay's max selection track the X chosen at cast
 * time, and `optional = true` lets "up to X" pick fewer (or zero) without fizzling. The chosen
 * instant/sorcery cards are returned to hand via [ForEachTargetEffect], then [selfExile] sends this
 * spell to exile instead of the graveyard on resolution.
 */
val DivergentEquation = card("Divergent Equation") {
    manaCost = "{X}{X}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return up to X target instant and/or sorcery cards from your graveyard to your " +
        "hand.\nExile Divergent Equation."

    spell {
        target = TargetObject(
            optional = true,
            filter = TargetFilter.InstantOrSorceryInYourGraveyard,
            dynamicMaxCount = DynamicAmount.XValue,
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND))
        )
        selfExile()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Inkognit"
        flavorText = "As Quandrix students advance in their studies, even slight miscalculations " +
            "can have startling effects."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26295e25-f1bf-4665-ba00-dad35c49bbc2.jpg?1775937210"
    }
}
