package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Splatter Technique
 * {1}{U}{U}{R}{R}
 * Sorcery
 * Choose one —
 * • Draw four cards.
 * • Splatter Technique deals 4 damage to each creature and planeswalker.
 */
val SplatterTechnique = card("Splatter Technique") {
    manaCost = "{1}{U}{U}{R}{R}"
    colorIdentity = "UR"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Draw four cards.\n" +
        "• Splatter Technique deals 4 damage to each creature and planeswalker."

    spell {
        modal(chooseCount = 1) {
            mode("Draw four cards") {
                effect = Effects.DrawCards(4)
            }
            mode("Splatter Technique deals 4 damage to each creature and planeswalker") {
                effect = Patterns.Group.dealDamageToAll(
                    4,
                    GroupFilter(GameObjectFilter.CreatureOrPlaneswalker)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Tuan Duong Chu"
        flavorText = "As the marvels of living art they are, elemental whales can't resist contributing to Summitfest's displays."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/969b6657-c3b9-47e1-a42e-95bbcccf452d.jpg?1775938612"
    }
}
