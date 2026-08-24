package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Deafening Clarion
 * {1}{R}{W}
 * Sorcery
 * Choose one or both —
 * • Deafening Clarion deals 3 damage to each creature.
 * • Creatures you control gain lifelink until end of turn.
 */
val DeafeningClarion = card("Deafening Clarion") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Deafening Clarion deals 3 damage to each creature.\n" +
        "• Creatures you control gain lifelink until end of turn."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Deafening Clarion deals 3 damage to each creature") {
                effect = Patterns.Group.dealDamageToAll(3, GroupFilter.AllCreatures)
            }
            mode("Creatures you control gain lifelink until end of turn") {
                effect = Patterns.Group.grantKeywordToAll(
                    Keyword.LIFELINK,
                    GroupFilter.AllCreaturesYouControl
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "165"
        artist = "Adam Paquette"
        flavorText = "\"Commander, what's the signal to attack?\"\n\"You'll know.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e115a81-001d-4e17-98af-6a63f2b0967f.jpg?1783934138"
    }
}
