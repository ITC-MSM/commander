package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters

/**
 * Rhizome Lurcher
 * {2}{B}{G}
 * Creature — Fungus Zombie
 * 2/2
 * Undergrowth — This creature enters with a number of +1/+1 counters on it equal to the number of creature cards in your graveyard.
 */
val RhizomeLurcher = card("Rhizome Lurcher") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Fungus Zombie"
    oracleText = "Undergrowth — This creature enters with a number of +1/+1 counters on it equal to the number of creature cards in your graveyard."
    power = 2
    toughness = 2

    replacementEffect(
        EntersWithDynamicCounters(count = DynamicAmounts.creatureCardsInYourGraveyard())
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Mathias Kollros"
        flavorText = "\"The dead gain new purpose here. What is strange in the eyes of other guilds is harmonious in ours.\"\n—Devesh, Golgari shaman"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db9ce92b-79cc-4e26-b511-30ae8ea6a2a1.jpg?1783934125"
    }
}
