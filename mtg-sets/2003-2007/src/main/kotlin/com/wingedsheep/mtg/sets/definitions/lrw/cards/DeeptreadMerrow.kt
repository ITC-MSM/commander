package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deeptread Merrow
 * {1}{U}
 * Creature — Merfolk Rogue
 * 2/1
 * {U}: This creature gains islandwalk until end of turn.
 */
val DeeptreadMerrow = card("Deeptread Merrow") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 2
    toughness = 1
    oracleText = "{U}: This creature gains islandwalk until end of turn. (It can't be blocked as " +
        "long as defending player controls an Island.)"

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.GrantKeyword(Keyword.ISLANDWALK, EffectTarget.Self)
        description = "{U}: This creature gains islandwalk until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Terese Nielsen & Philip Tan"
        flavorText = "\"My success at navigating the Dark Meanders irritates the Inkfathom school. They consider themselves peerless divers, but I try to remind them that they cannot own commodities like bravery and cunning.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42ebd6ff-2b1c-4e93-81cc-81998459c5c7.jpg?1783942904"
    }
}
