package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Paladin of Predation
 * {5}{G}{G}
 * Creature — Phyrexian Knight
 * 6/7
 *
 * Toxic 6 (Players dealt combat damage by this creature also get six poison counters.)
 * This creature can't be blocked by creatures with power 2 or less.
 *
 * Toxic must be a [KeywordAbility.Numeric] — the projector only emits the numbered toxic marker
 * from the numeric form.
 */
val PaladinOfPredation = card("Paladin of Predation") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Knight"
    power = 6
    toughness = 7
    oracleText = "Toxic 6 (Players dealt combat damage by this creature also get six poison counters.)\n" +
        "This creature can't be blocked by creatures with power 2 or less."

    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 6))

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Lorenzo Mastroianni"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/758dbe61-6dc7-4b08-bdd6-7262257955fc.jpg?1783918012"
    }
}
