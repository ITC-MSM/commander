package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Cosmogoyf
 * {B}{G}
 * Creature — Elemental Lhurgoyf
 *
 * Cosmogoyf's power is equal to the number of cards you own in exile and its
 * toughness is equal to that number plus 1.
 *
 * The Tarmogoyf-style star-over-star-plus-one body composed out of existing primitives — the
 * dynamic power/toughness read the source's controller's exile sub-zone via
 * [DynamicAmounts.zone] + COUNT, and `dynamicStats(toughnessOffset = 1)` wires
 * the same source as a base-set characteristic-defining value through
 * [com.wingedsheep.sdk.model.CharacteristicValue.DynamicWithOffset].
 */
val Cosmogoyf = card("Cosmogoyf") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elemental Lhurgoyf"
    dynamicStats(
        DynamicAmounts.zone(Player.You, Zone.EXILE).count(),
        toughnessOffset = 1,
    )
    oracleText = "Cosmogoyf's power is equal to the number of cards you own in exile and " +
        "its toughness is equal to that number plus 1."

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "215"
        artist = "Chris Rahn"
        flavorText = ">Alert: COSMOGOYF\n>Recommended Action: RUN\n—PSS *Erix* shipboard computer, final transmission"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e07d3c6-60a5-44d1-a926-6414be85bd50.jpg?1752947436"
    }
}
