package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Erratic Visionary
 * {1}{U}
 * Creature — Human Wizard
 * 1/3
 *
 * {1}{U}, {T}: Draw a card, then discard a card.
 */
val ErraticVisionary = card("Erratic Visionary") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "{1}{U}, {T}: Draw a card, then discard a card."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        effect = Effects.DrawCards(1).then(Effects.Discard(1))
        description = "{1}{U}, {T}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Randy Vargas"
        flavorText = "An Izzet experiment begins with a \"what if,\" gets approved with a \"why not,\" and concludes with a \"eureka!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03188bc3-0ef1-40cd-9c3c-4bb4d806fb92.jpg?1783933466"
    }
}
