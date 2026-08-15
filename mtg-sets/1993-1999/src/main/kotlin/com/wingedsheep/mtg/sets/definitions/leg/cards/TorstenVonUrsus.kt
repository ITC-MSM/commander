package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Torsten Von Ursus
 * {3}{G}{G}{W}
 * Legendary Creature — Human Soldier
 * 5/5
 *
 * Vanilla — no rules text.
 */
val TorstenVonUrsus = card("Torsten Von Ursus") {
    manaCost = "{3}{G}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Human Soldier"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "266"
        artist = "Mark Poole"
        flavorText = "\"How can you accuse me of evil? Though these deeds be unsavory, no one will argue: good shall follow from them.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fd99522-4a91-4ccd-91bf-5f32a6ac3510.jpg?1783948031"
    }
}
