package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mawcor
 * {3}{U}{U}
 * Creature — Beast
 * 3/3
 * Flying
 * {T}: This creature deals 1 damage to any target.
 */
val Mawcor = card("Mawcor") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{T}: This creature deals 1 damage to any target."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        val victim = target("target", Targets.Any)
        effect = Effects.DealDamage(1, victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "John Matson"
        flavorText = "From its maw comes neither word nor whisper—only wind."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f50971e-2a18-4db7-8b5b-83dd5e85766e.jpg"
    }
}
