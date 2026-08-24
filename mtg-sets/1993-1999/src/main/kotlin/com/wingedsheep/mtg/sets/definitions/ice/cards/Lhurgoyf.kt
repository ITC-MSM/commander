package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lhurgoyf
 * {2}{G}{G}
 * Creature — Lhurgoyf
 * Characteristic-defining power/toughness — see dynamicStats below.
 *
 * Lhurgoyf's power is equal to the number of creature cards in all graveyards and its toughness is
 * equal to that number plus 1.
 *
 * The original *-body: a characteristic-defining ability (CR 604.3), so it is `dynamicStats` rather
 * than a printed P/T plus a pump — one [DynamicAmount] feeds base power and base toughness in layer
 * 7a and functions in every zone. [Player.Each] is what makes "all graveyards" all of them, and the
 * "plus 1" is `toughnessOffset` on that same amount rather than a second value, so the two halves
 * can never drift apart. Soulless One reads graveyards the same way.
 */
val Lhurgoyf = card("Lhurgoyf") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lhurgoyf"
    dynamicStats(
        DynamicAmount.Count(Player.Each, Zone.GRAVEYARD, GameObjectFilter.Creature),
        toughnessOffset = 1
    )
    oracleText = "Lhurgoyf's power is equal to the number of creature cards in all graveyards and " +
        "its toughness is equal to that number plus 1."

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "252"
        artist = "Pete Venters"
        flavorText = "\"Ach! Hans, run! It's the Lhurgoyf!\"\n—Saffi Eriksdotter, last words"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fee6d385-d44b-4f1a-beb1-13aeebde063e.jpg"
    }
}
