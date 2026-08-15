package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Basking Capybara — {1}{G}
 * Creature — Capybara
 * 1/3
 * Descend 4 — This creature gets +3/+0 as long as there are four or more permanent cards
 * in your graveyard.
 */
val BaskingCapybara = card("Basking Capybara") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Capybara"
    oracleText = "Descend 4 — This creature gets +3/+0 as long as there are four or more permanent cards in your graveyard."
    power = 1
    toughness = 3

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 3, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff0a2ba4-dfa8-49d6-95e9-04b7a14d0c6c.jpg?1782694470"
    }
}
