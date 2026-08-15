package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bygone Colossus
 * {9}
 * Artifact Creature — Robot Giant
 * Warp {3}
 * 9/9
 */
val BygoneColossus = card("Bygone Colossus") {
    manaCost = "{9}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Robot Giant"
    power = 9
    toughness = 9
    oracleText = "Warp {3} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"

    warp = "{3}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Maxime Minard"
        flavorText = "The Edge is littered with the vestiges of civilizations annihilated by the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4bb8f2ef-4398-4a07-9130-5005356a3b4a.jpg?1752947518"
    }
}
