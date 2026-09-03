package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Enforcer Griffin
 * {4}{W}
 * Creature — Griffin
 * 3/4
 * Flying
 */
val EnforcerGriffin = card("Enforcer Griffin") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    oracleText = "Flying"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Johan Grenier"
        flavorText = "\"A company of infantry is trapped behind the lines. We need to strike hard and fast to free them, or the casualties will be horrific. Send the griffins.\"\n—Tajic"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4d6d488-01df-4d61-92ff-9881838b4018.jpg"
    }
}
