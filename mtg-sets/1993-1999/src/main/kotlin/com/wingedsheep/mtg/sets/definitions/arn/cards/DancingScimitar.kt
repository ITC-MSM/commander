package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dancing Scimitar
 * {4}
 * Artifact Creature — Spirit
 * 1/5
 * Flying
 */
val DancingScimitar = card("Dancing Scimitar") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Spirit"
    power = 1
    toughness = 5
    oracleText = "Flying"
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "61"
        artist = "Anson Maddocks"
        flavorText = "Bobbing merrily from opponent to opponent, the scimitar began adding playful little flourishes to its strokes; it even turned a couple of somersaults."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb2e494-1414-4d1f-91d2-7cb20acdb128.jpg?1562900683"
    }
}
