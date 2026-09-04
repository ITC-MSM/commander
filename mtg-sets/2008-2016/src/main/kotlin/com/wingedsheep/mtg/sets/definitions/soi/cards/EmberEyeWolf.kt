package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ember-Eye Wolf (Shadows over Innistrad #154)
 * {1}{R}
 * Creature — Wolf
 * 1 / 2
 *
 * Haste
 * {1}{R}: This creature gets +2/+0 until end of turn.
 */
val EmberEyeWolf = card("Ember-Eye Wolf") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wolf"
    power = 1
    toughness = 2
    oracleText = "Haste\n" +
        "{1}{R}: This creature gets +2/+0 until end of turn."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Anthony Palumbo"
        flavorText = "No howl. No snarl. Just the roar of flames."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98fe1e1e-b14a-4efe-894b-b9da635f007f.jpg?1783937754"
    }
}
