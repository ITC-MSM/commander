package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ziatora's Proving Ground
 * Land — Swamp Mountain Forest
 * ({T}: Add {B}, {R}, or {G}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val ZiatorasProvingGround = card("Ziatora's Proving Ground") {
    colorIdentity = "BGR"
    typeLine = "Land — Swamp Mountain Forest"
    oracleText = "({T}: Add {B}, {R}, or {G}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "261"
        artist = "Viko Menezes"
        flavorText = "Restless Riveteers can always find a sparring partner in the sprawling Treza warehouse."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75fdce80-e338-4a50-bdc6-786511feaeef.jpg?1783923052"
    }
}
