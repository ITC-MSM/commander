package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * War-Name Aspirant
 * {1}{R}
 * Creature — Human Warrior
 * 2/1
 * Raid — War-Name Aspirant enters the battlefield with a +1/+1 counter on it
 * if you attacked this turn.
 * War-Name Aspirant can't be blocked by creatures with power 1 or less.
 */
val WarNameAspirant = card("War-Name Aspirant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 1
    oracleText = "Raid — This creature enters with a +1/+1 counter on it if you attacked this turn.\nThis creature can't be blocked by creatures with power 1 or less."

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = Conditions.YouAttackedThisTurn
    ))

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "David Gaillet"
        flavorText = "\"No battle means more to a Mardu warrior than the one that earns her war name.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8d1714b-ff65-4c5c-ad21-b469f2c72286.jpg?1562795326"
    }
}
