package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nomadic Elf
 * {1}{G}
 * Creature — Elf Nomad
 * 2/2
 * {1}{G}: Add one mana of any color.
 */
val NomadicElf = card("Nomadic Elf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Nomad"
    oracleText = "{1}{G}: Add one mana of any color."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "D. J. Cleland-Hura"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b69e57a-5b19-450c-9cf5-c189e8505781.jpg?1562907011"
    }
}
