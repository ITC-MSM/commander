package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersAsCopy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Superior Spider-Man
 * {2}{U}{B}
 * Legendary Creature — Spider Human Hero
 * 4/4
 * Mind Swap — You may have Superior Spider-Man enter as a copy of any creature card in a graveyard,
 * except his name is Superior Spider-Man and he's a 4/4 Spider Human Hero in addition to his other
 * types. When you do, exile that card.
 *
 * "Mind Swap" is an ability word (flavor only). The mechanical behavior is a graveyard-sourced
 * [EntersAsCopy]: the copy keeps Superior Spider-Man's name, is forced to 4/4, gains the Spider/
 * Human/Hero creature types on top of the copied card's types, and the copied card is exiled.
 */
val SuperiorSpiderMan = card("Superior Spider-Man") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Spider Human Hero"
    power = 4
    toughness = 4
    oracleText = "Mind Swap — You may have Superior Spider-Man enter as a copy of any creature card " +
        "in a graveyard, except his name is Superior Spider-Man and he's a 4/4 Spider Human Hero in " +
        "addition to his other types. When you do, exile that card."

    replacementEffect(
        EntersAsCopy(
            optional = true,
            copyFilter = GameObjectFilter.Creature,
            copyFromZone = Zone.GRAVEYARD,
            additionalSubtypes = listOf("Spider", "Human", "Hero"),
            nameOverride = "Superior Spider-Man",
            powerOverride = 4,
            toughnessOverride = 4,
            exileCopiedCard = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "155"
        artist = "Carlos Dattoli"
        flavorText = "\"I will be Spider-Man. With my unparalleled genius, I shall become the *Superior* Spider-Man.\"\n—Otto Octavius"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad4adc3e-ec41-4406-8ff2-59ba8067cf4e.jpg?1758203859"
    }
}
