package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Harrier Naga
 * {2}{G}
 * Creature — Snake Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val HarrierNaga = card("Harrier Naga") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Filip Burburan"
        flavorText = "She trusts that the potent poisons of her darts will reach the enemy before the enemy reaches her."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bcdc68c9-f5f3-4c5b-80df-85508cf15f84.jpg?1783936019"
    }
}
