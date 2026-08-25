package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Senate Courier — Ravnica Allegiance #50
 * {2}{U} · Creature — Bird · 1 / 4
 *
 * Flying is printed; vigilance is granted by the activation until end of turn.
 */
val SenateCourier = card("Senate Courier") {
    manaCost = "{2}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 4
    oracleText = "Flying\n" +
        "{1}{W}: This creature gains vigilance until end of turn."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Johann Bodin"
        flavorText = "\"This Dovin Baan came from nowhere. Watch him. Read his letters. He is more than he appears.\"\n" +
        "—Lazav"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da8dc5c8-4eb7-4e41-8431-b41251f7814e.jpg"
    }
}
