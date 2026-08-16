package com.wingedsheep.mtg.sets.definitions.csp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Simian Brawler
 * {3}{G}
 * Creature — Ape Warrior
 * 3/3
 * Discard a land card: This creature gets +1/+1 until end of turn.
 */
val SimianBrawler = card("Simian Brawler") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ape Warrior"
    power = 3
    toughness = 3
    oracleText = "Discard a land card: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Land)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Warren Mahy"
        flavorText = "\"It's odd to see the apes rip down trees to arm themselves in defense of their forests.\" —Taaveti of Kelsinko, elvish hunter"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df2ed9f3-50b1-493b-9c14-0f8ddb4d8c57.jpg?1783943326"
    }
}
