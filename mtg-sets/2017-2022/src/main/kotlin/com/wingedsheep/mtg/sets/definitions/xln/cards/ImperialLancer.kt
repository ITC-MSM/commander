package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Imperial Lancer
 * {W}
 * Creature — Human Knight
 * 1/1
 *
 * This creature has double strike as long as you control a Dinosaur.
 */
val ImperialLancer = card("Imperial Lancer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "This creature has double strike as long as you control a Dinosaur."
    power = 1
    toughness = 1

    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, GroupFilter.source())
        condition = Conditions.ControlPermanentOfType(Subtype.DINOSAUR)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Viktor Titov"
        flavorText = "\"Together my mount and I are stronger than either of us apart.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ed3784a-2dc0-4626-aa6a-268c1e2ac83c.jpg"
    }
}
