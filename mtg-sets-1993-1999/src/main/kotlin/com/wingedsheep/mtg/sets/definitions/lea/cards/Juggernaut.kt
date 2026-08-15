package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustAttack

/**
 * Juggernaut
 * {4}
 * Artifact Creature — Juggernaut
 * 5/3
 * Juggernaut attacks each combat if able.
 * Juggernaut can't be blocked by Walls.
 */
val Juggernaut = card("Juggernaut") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Juggernaut"
    power = 5
    toughness = 3
    oracleText = "Juggernaut attacks each combat if able.\nJuggernaut can't be blocked by Walls."

    staticAbility {
        ability = MustAttack()
    }

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withSubtype("Wall"))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "255"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcd6a291-5282-4f49-8203-d9b416083c48.jpg?1559591652"
    }
}
