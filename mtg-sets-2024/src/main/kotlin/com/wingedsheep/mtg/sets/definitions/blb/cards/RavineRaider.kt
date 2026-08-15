package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ravine Raider {B}
 * Creature — Lizard Rogue
 * 1/1
 *
 * Menace
 * {1}{B}: This creature gets +1/+1 until end of turn.
 */
val RavineRaider = card("Ravine Raider") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Lizard Rogue"
    power = 1
    toughness = 1
    oracleText = "Menace\n{1}{B}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Simon Dominic"
        flavorText = "The path to the Cliff of Heroes is rife with bandits, expertly camouflaged in the dense flora of Valley."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/874510be-7ecd-4eff-abad-b9594eb4821a.jpg?1721426478"
    }
}