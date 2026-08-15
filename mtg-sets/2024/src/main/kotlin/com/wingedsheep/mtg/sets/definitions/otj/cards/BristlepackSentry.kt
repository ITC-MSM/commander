package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Bristlepack Sentry
 * {1}{G}
 * Creature — Plant Wolf
 * 3/3
 * Defender
 * As long as you control a creature with power 4 or greater, this creature can attack
 * as though it didn't have defender.
 */
val BristlepackSentry = card("Bristlepack Sentry") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wolf"
    power = 3
    toughness = 3
    oracleText = "Defender\nAs long as you control a creature with power 4 or greater, this creature can attack as though it didn't have defender."

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = CanAttackDespiteDefender(
            condition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Josiah \"Jo\" Cameron"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6aea6702-16a8-4073-9c83-fbea20a5fd32.jpg?1712355891"

        ruling("2024-04-12", "Once Bristlepack Sentry has legally attacked, causing its last ability to not apply by removing other creatures from the battlefield or reducing the power of one or more creatures won't cause Bristlepack Sentry to stop attacking.")
        ruling("2024-04-12", "If you raise Bristlepack Sentry's power to 4 or greater, it fulfills its own condition and it can attack as though it didn't have defender.")
    }
}
