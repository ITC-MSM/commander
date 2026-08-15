package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnlessCoAttacker
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Scarred Puma
 * {R}
 * Creature — Cat
 * 2/1
 *
 * This creature can't attack unless a black or green creature also attacks.
 */
val ScarredPuma = card("Scarred Puma") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 1
    oracleText = "This creature can't attack unless a black or green creature also attacks."

    staticAbility {
        ability = CantAttackUnlessCoAttacker(
            coAttackerFilter = GameObjectFilter.Creature.withAnyColor(Color.BLACK, Color.GREEN)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Aaron Boyd"
        flavorText = "It's not eager to lose the *other* eye."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/067ff95e-c4dc-41bb-9677-67f51a09b05a.jpg?1562896303"
    }
}
