package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hollowhenge Beast
 * {3}{G}{G}
 * Creature — Beast
 * 5/5
 *
 * Vanilla — no rules text.
 */
val HollowhengeBeast = card("Hollowhenge Beast") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Dave Kendall"
        flavorText = "In a world of monsters, it's the stuff of nightmares."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/052ab91f-ac01-43f4-9276-9af35dbfbf71.jpg?1783940805"
    }
}
