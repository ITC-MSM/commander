package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Heroes Remembered
 * {6}{W}{W}{W}
 * Sorcery
 * You gain 20 life.
 * Suspend 10—{W}
 */
val HeroesRemembered = card("Heroes Remembered") {
    manaCost = "{6}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "You gain 20 life.\n" +
        "Suspend 10—{W} (Rather than cast this card from your hand, you may pay {W} and exile it with ten time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)"

    keywordAbility(KeywordAbility.suspend("{W}", 10))

    spell {
        effect = Effects.GainLife(20)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Michael Phillippi"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9902b260-82cf-4b10-a353-321231824a3b.jpg"
    }
}
