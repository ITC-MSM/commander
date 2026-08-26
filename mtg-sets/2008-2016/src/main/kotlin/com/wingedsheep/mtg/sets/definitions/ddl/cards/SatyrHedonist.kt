package com.wingedsheep.mtg.sets.definitions.ddl.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Satyr Hedonist
 * {1}{G}
 * Creature — Satyr
 * 2 / 1
 *
 * {R}, Sacrifice this creature: Add {R}{R}{R}.
 *
 * Duel Decks: Heroes vs. Monsters is this card's earliest printing, so the canonical definition
 * lives here rather than in Theros. `manaAbility = true` is the whole timing declaration — the
 * builder derives `TimingRule.ManaAbility` from it, so writing the timing out again would be a
 * second authoring choice for one fact.
 */
val SatyrHedonist = card("Satyr Hedonist") {
    manaCost = "{1}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Satyr"
    power = 2
    toughness = 1
    oracleText = "{R}, Sacrifice this creature: Add {R}{R}{R}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.SacrificeSelf)
        manaAbility = true
        effect = Effects.AddMana(Color.RED, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Chase Stone"
        flavorText = "\"Any festival you can walk away from wasn't worth attending in the first place.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c6a5405-2bc8-4439-a09a-2c4844ba3a35.jpg"
    }
}
