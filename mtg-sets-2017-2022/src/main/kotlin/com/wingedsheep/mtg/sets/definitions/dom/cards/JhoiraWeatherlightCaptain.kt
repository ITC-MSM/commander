package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jhoira, Weatherlight Captain
 * {2}{U}{R}
 * Legendary Creature — Human Artificer
 * 3/3
 * Whenever you cast a historic spell, draw a card.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val JhoiraWeatherlightCaptain = card("Jhoira, Weatherlight Captain") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Artificer"
    power = 3
    toughness = 3
    oracleText = "Whenever you cast a historic spell, draw a card. (Artifacts, legendaries, and Sagas are historic.)"

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "197"
        artist = "Brad Rigney"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73cf8c6b-1322-4bc5-a604-6e372607fae4.jpg?1595568330"
        ruling("2020-08-07", "A spell is historic if it has the legendary supertype, the artifact card type, or the Saga enchantment subtype.")
        ruling("2020-08-07", "An ability that triggers when a player casts a spell resolves before the spell that caused it to trigger. It resolves even if that spell is countered.")
    }
}
