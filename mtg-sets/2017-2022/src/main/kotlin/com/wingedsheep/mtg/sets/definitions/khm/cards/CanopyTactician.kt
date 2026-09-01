package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Canopy Tactician
 * {3}{G}
 * Creature — Elf Warrior
 * 3/3
 * Other Elves you control get +1/+1.
 * {T}: Add {G}{G}{G}.
 *
 * An Elf lord that is also a three-mana rock. The lord filter is [GameObjectFilter.Permanent]
 * rather than `.Creature` because the printed line says "Other Elves", which reaches a noncreature
 * Elf permanent too; `excludeSelf = true` carries the printed "Other".
 */
val CanopyTactician = card("Canopy Tactician") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "Other Elves you control get +1/+1.\n" +
        "{T}: Add {G}{G}{G}."
    power = 3
    toughness = 3

    // Other Elves you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.ELF).youControl(),
                excludeSelf = true
            )
        )
    }

    // {T}: Add {G}{G}{G}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN, 3)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "378"
        artist = "Ekaterina Burmak"
        flavorText = "\"Death and life are two ends of the same spear.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3eaf48c9-09bc-4d81-a3a5-432219a71754.jpg"
    }
}
