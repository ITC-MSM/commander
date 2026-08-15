package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Expedition Envoy
 * {W}
 * Creature — Human Scout Ally
 * 2/1
 *
 * Vanilla — no rules text.
 */
val ExpeditionEnvoy = card("Expedition Envoy") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout Ally"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "David Palumbo"
        flavorText = "The unofficial motto of every expedition house is \"ready for anything,\" a phrase whose significance has been amplified since the emergence of the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41193ef1-1619-4448-9905-26b05079c79a.jpg?1783938221"
    }
}
