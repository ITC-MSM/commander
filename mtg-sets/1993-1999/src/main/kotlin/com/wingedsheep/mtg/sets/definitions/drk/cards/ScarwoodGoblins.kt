package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scarwood Goblins
 * {R}{G}
 * Creature — Goblin
 * 2/2
 *
 * Vanilla — no rules text.
 */
val ScarwoodGoblins = card("Scarwood Goblins") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Goblin"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Ron Spencer"
        flavorText = "Larger and more cunning than most Goblins, Scarwood Goblins are thankfully found only in isolated pockets."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/5542d236-af43-43b8-b30f-8980d74bbdd0.jpg?1783947928"
    }
}
