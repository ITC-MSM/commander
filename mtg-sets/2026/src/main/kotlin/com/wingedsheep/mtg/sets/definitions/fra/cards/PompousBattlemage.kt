package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

val PompousBattlemage = card("Pompous Battlemage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Sorcerer"
    power = 1
    toughness = 1
    oracleText = "Prowess\nThis creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    prowess()
    keywords(Keyword.PREPARED)

    prepare("Improvised Act") {
        manaCost = "{R}"
        typeLine = "Sorcery"
        oracleText = "You may discard a card. If you do, draw a card."
        spell {
            effect = MayEffect(
                effect = IfYouDoEffect(
                    action = Patterns.Hand.discardCards(1),
                    ifYouDo = Effects.DrawCards(1),
                ),
                descriptionOverride = "You may discard a card. If you do, draw a card.",
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "90"
        artist = "Justin Gerard"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa0f77ac-741a-444a-8bf0-a42c644726bf.jpg?1788878186"
        inBooster = false
    }
}
