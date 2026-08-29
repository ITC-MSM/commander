package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Outland Colossus
 * {3}{G}{G}
 * Creature — Giant
 * 6/6
 *
 * Renown 6 (When this creature deals combat damage to a player, if it isn't renowned, put six +1/+1 counters on it and it becomes renowned.)
 * This creature can't be blocked by more than one creature.
 */
val OutlandColossus = card("Outland Colossus") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Giant"
    oracleText = "Renown 6 (When this creature deals combat damage to a player, if it isn't renowned, put six +1/+1 counters on it and it becomes renowned.)\n" +
        "This creature can't be blocked by more than one creature."
    power = 6
    toughness = 6

    keywordAbility(KeywordAbility.renown(6))

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "193"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1caad298-52cb-46f1-8212-fe657ab80159.jpg"
    }
}
