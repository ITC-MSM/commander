package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GrantChosenColor
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Alloy Golem
 * {6}
 * Artifact Creature — Golem
 * 4/4
 *
 * As this creature enters, choose a color.
 * This creature is the chosen color. (It's still an artifact.)
 *
 * The golem starts colorless, so "adding" the chosen color (Layer 5) is equivalent
 * to it becoming that color while remaining an artifact.
 */
val AlloyGolem = card("Alloy Golem") {
    manaCost = "{6}"
    typeLine = "Artifact Creature — Golem"
    power = 4
    toughness = 4
    oracleText = "As this creature enters, choose a color.\n" +
        "This creature is the chosen color. (It's still an artifact.)"

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    staticAbility {
        ability = GrantChosenColor(GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "297"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fb6d6a1-9d71-405b-9c93-1a7f06c67abd.jpg?1562901306"
    }
}
