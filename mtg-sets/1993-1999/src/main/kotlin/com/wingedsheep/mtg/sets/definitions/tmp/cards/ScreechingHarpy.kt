package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Screeching Harpy
 * {2}{B}{B}
 * Creature — Harpy Beast
 * 2/2
 * Flying
 * {1}{B}: Regenerate this creature.
 */
val ScreechingHarpy = card("Screeching Harpy") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Harpy Beast"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "{1}{B}: Regenerate this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Una Fricker"
        flavorText = "\"They are called 'fowl' for a reason.\"\n" +
            "—Mirri of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10c02902-4e3a-445e-9dd9-116806ddc966.jpg"
    }
}
