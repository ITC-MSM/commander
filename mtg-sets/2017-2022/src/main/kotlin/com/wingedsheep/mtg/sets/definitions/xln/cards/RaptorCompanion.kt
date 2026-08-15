package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raptor Companion
 * {1}{W}
 * Creature — Dinosaur
 * 3/1
 *
 * Vanilla — no rules text.
 */
val RaptorCompanion = card("Raptor Companion") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Slawomir Maniak"
        flavorText = "A raptor will follow any order as long as that order is \"hunt,\" \"kill,\" or \"go for the guts.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d37fc42-0a7c-46b3-9270-333d370e479f.jpg?1783935794"
    }
}
