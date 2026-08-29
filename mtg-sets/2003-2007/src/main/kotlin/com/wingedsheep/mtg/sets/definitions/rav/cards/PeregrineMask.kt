package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Peregrine Mask
 * {1}
 * Artifact — Equipment
 * Equipped creature has defender, flying, and first strike.
 * Equip {2}
 *
 * Three separate [GrantKeyword] statics rather than one multi-keyword ability: the SDK keeps one
 * keyword per grant, and each defaults to the equipped creature. `equipAbility` sets `equipCost`
 * and lowers the sorcery-speed attach ability in one place.
 */
val PeregrineMask = card("Peregrine Mask") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has defender, flying, and first strike.\n" +
        "Equip {2}"

    staticAbility {
        ability = GrantKeyword(Keyword.DEFENDER)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "268"
        artist = "Edward P. Beard, Jr."
        flavorText = "The mask confers both the prowess of a falcon and its loyalty."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9196f43e-f905-4e83-8e47-9d8fd53a4c9f.jpg"
    }
}
