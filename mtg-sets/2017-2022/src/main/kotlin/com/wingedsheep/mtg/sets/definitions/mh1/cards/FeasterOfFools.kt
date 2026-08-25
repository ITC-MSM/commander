package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Feaster of Fools
 * {4}{B}{B}
 * Creature — Demon
 * 3/3
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Flying
 * Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with twice that many +1/+1 counters on it.)
 *
 * Convoke and flying are bare keywords the engine already reads; devour is the odd one out and takes
 * two declarations, exactly as `ala/cards/ThunderThrashElder.kt` spells it. [KeywordAbility.devour]
 * gives the printed line, while the [EntersWithDevour] replacement effect is what actually offers
 * the sacrifice and stamps `2 ×` that many +1/+1 counters as the Demon enters. Its defaults — the
 * plain creature sacrifice filter and the unnamed variant — are the printed "devour 2".
 */
val FeasterOfFools = card("Feaster of Fools") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 3
    toughness = 3
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Flying\n" +
        "Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with twice that many +1/+1 counters on it.)"

    keywords(Keyword.CONVOKE, Keyword.FLYING, Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(2))

    replacementEffect(EntersWithDevour(multiplier = 2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "John Severin Brassell"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4472ad84-b548-4ed8-a315-ecf9ba9d49ff.jpg?1783933128"
    }
}
