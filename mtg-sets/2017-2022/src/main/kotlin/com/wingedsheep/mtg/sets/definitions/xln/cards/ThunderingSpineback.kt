package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Thundering Spineback
 * {5}{G}{G}
 * Creature — Dinosaur
 * 5/5
 *
 * Other Dinosaurs you control get +1/+1.
 * {5}{G}: Create a 3/3 green Dinosaur creature token with trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val ThunderingSpineback = card("Thundering Spineback") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Other Dinosaurs you control get +1/+1.\n{5}{G}: Create a 3/3 green Dinosaur creature token with trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)"
    power = 5
    toughness = 5

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.DINOSAUR).youControl(),
                excludeSelf = true,
            ),
        )
    }

    activatedAbility {
        cost = Costs.Mana("{5}{G}")
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dinosaur"),
            keywords = setOf(Keyword.TRAMPLE),
        )
        description = "{5}{G}: Create a 3/3 green Dinosaur creature token with trample."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Tomasz Jedruszek"
        flavorText = "\"It appears that nature has risen against us.\"\n—Captain Brinely Rage"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35711da6-aac4-4c2a-b324-aebeaa843adc.jpg?1783935718"
    }
}
