package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Iridescent Blademaster
 * {1}{G}
 * Creature — Elf Warrior
 * 2/2
 * {3}{G}: This creature gets +2/+2 until end of turn.
 */
val IridescentBlademaster = card("Iridescent Blademaster") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "{3}{G}: This creature gets +2/+2 until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Livia Prima"
        flavorText = "Her arm and spirit were weary, but as the Halo flowed across her blade, it " +
            "became weightless, and new strength flooded through her. She would fight on."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3fee189f-539f-48fa-b217-4b2599375364.jpg?1783916966"
    }
}
