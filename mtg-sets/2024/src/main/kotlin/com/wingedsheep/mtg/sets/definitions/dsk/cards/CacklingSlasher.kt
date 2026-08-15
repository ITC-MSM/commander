package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Cackling Slasher
 * {3}{B}
 * Creature — Human Assassin
 * 3/3
 * Deathtouch
 * This creature enters with a +1/+1 counter on it if a creature died this turn.
 */
val CacklingSlasher = card("Cackling Slasher") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Assassin"
    power = 3
    toughness = 3
    oracleText = "Deathtouch\nThis creature enters with a +1/+1 counter on it if a creature died this turn."

    keywords(Keyword.DEATHTOUCH)

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = Conditions.CreatureDiedThisTurn
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Johann Bodin"
        flavorText = "Every death was funnier than the last, until his howling laughter drowned out his victims' screams."
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1269ccf-febf-42b0-8c20-21ccb731a3ec.jpg?1726286167"
    }
}
