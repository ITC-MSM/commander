package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Ritual of Soot
 * {2}{B}{B}
 * Sorcery
 * Destroy all creatures with mana value 3 or less.
 */
val RitualOfSoot = card("Ritual of Soot") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures with mana value 3 or less."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature.manaValueAtMost(3))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "84"
        artist = "Dimitar Marinski"
        flavorText = "Only the patrol's armor was found, so tainted with the acrid smell of sudden death that it could never be worn again."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/269af993-4894-4bf1-b55a-af4d736cb3cc.jpg?1783934172"
    }
}
