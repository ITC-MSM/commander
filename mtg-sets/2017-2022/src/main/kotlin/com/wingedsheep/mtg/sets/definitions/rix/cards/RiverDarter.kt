package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * River Darter
 * {2}{U}
 * Creature — Merfolk Warrior
 * 2/3
 * This creature can't be blocked by Dinosaurs.
 *
 * The blocker noun is the bare tribal "Dinosaurs", i.e. Dinosaur *permanents* —
 * `GameObjectFilter.Permanent.withSubtype`, not `.Creature`.
 */
val RiverDarter = card("River Darter") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "This creature can't be blocked by Dinosaurs."
    power = 2
    toughness = 3

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Permanent.withSubtype(Subtype.DINOSAUR))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Winona Nelson"
        flavorText = "\"Don't make splashes. Make progress.\"\n—Tishana"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d766eb87-19ef-460b-982f-e55ae5890e6a.jpg?1783935322"
    }
}
