package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Devkarin Dissident
 * {1}{G}
 * Creature — Elf Warrior
 * 2/2
 * {4}{G}: This creature gets +2/+2 until end of turn.
 */
val DevkarinDissident = card("Devkarin Dissident") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "{4}{G}: This creature gets +2/+2 until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Mark Zug"
        flavorText = "\"This is Mileva, in the Tenth. We've got an elf in the plaza with a chip on her shoulder. Actually, it's more of a morningstar.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/9/490cd287-5f09-442f-9150-4a6ac2cf3e2e.jpg?1783934153"
    }
}
