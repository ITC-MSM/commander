package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost

/**
 * Kwia Vigorbloom — the Lotus is a predefined token (a token Black Lotus). Kwia's own lifelink
 * gains feed the trigger, but "only once each turn" caps it at one Lotus per turn however many
 * separate life-gain events happen.
 */
val KwiaVigorbloom = card("Kwia Vigorbloom") {
    manaCost = "{3}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Elder Sphinx"
    power = 6
    toughness = 6
    oracleText = "Flying, vigilance, lifelink, ward {2}\n" +
        "Whenever you gain life, create a colorless artifact token named Lotus with \"{T}, Sacrifice this " +
        "token: Add three mana of any one color.\" This ability triggers only once each turn."

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.LIFELINK)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.CreateLotus()
        oncePerTurn = true
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "140"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d6ff182-a853-4898-895b-072c89324ca7.jpg?1788878236"
        inBooster = false
    }
}
