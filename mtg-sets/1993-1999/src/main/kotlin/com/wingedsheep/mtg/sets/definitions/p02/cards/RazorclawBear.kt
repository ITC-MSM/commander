package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Razorclaw Bear
 * {2}{G}{G}
 * Creature — Bear
 * 3 / 3
 *
 * Whenever this creature becomes blocked, it gets +2/+2 until end of turn.
 */
val RazorclawBear = card("Razorclaw Bear") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    oracleText = "Whenever this creature becomes blocked, it gets +2/+2 until end of turn."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "142"
        artist = "Heather Hudson"
        flavorText = "One razorclaw is three bears too many.\n—Elvish saying"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08c0cdbe-ca74-4edc-96ea-6db8eefe99d8.jpg"
    }
}
