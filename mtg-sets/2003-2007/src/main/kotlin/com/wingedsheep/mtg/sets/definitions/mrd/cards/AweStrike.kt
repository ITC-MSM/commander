package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/** Awe Strike — Mirrodin #6. */
val AweStrike = card("Awe Strike") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "The next time target creature would deal damage this turn, prevent that damage. " +
        "You gain life equal to the damage prevented this way."

    spell {
        target = Targets.Creature
        effect = Effects.PreventNextDamageDealtBy(
            target = EffectTarget.ContextTarget(0),
            onPrevented = Effects.GainLife(DynamicAmounts.preventedDamage())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Scott M. Fischer"
        flavorText = "Stunned by the mere presence of the leonin kha, the nim raider quickly fell to its knees."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5cd231b-7891-42b5-b5de-5112d1230c37.jpg?1783944563"
    }
}
