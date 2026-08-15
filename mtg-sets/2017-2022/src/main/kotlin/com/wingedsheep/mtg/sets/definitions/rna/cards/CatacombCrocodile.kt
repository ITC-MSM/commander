package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Catacomb Crocodile
 * {4}{B}
 * Creature — Crocodile
 * 3/7
 *
 * Vanilla — no rules text.
 */
val CatacombCrocodile = card("Catacomb Crocodile") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Crocodile"
    power = 3
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Nils Hamm"
        flavorText = "\"I am sewer-king!\" said Rat. \"I am quick and cunning and I know every tunnel.\"\n\"No, I am king!\" said Zombie. \"I am cold and deadly and no rot can harm me.\"\nThen Croc came and ate them both."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/440c53f0-7922-4e14-802d-d7a22f8fed85.jpg?1783933696"
    }
}
