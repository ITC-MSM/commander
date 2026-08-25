package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thunder-Thrash Elder
 * {2}{R}
 * Creature — Lizard Warrior
 * 1 / 1
 * Devour 3 (As this creature enters, you may sacrifice any number of creatures. It enters with three times that many +1/+1 counters on it.)
 *
 * A vanilla body plus devour, and devour is two declarations rather than one:
 * [KeywordAbility.devour] gives the printed line and the keyword the rest of the engine reads,
 * while the [EntersWithDevour] replacement effect is what actually offers the sacrifice and stamps
 * `3 ×` that many +1/+1 counters as the creature enters. Its defaults — the plain creature filter
 * and the unnamed variant — are exactly the printed "devour 3".
 */
val ThunderThrashElder = card("Thunder-Thrash Elder") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Warrior"
    power = 1
    toughness = 1
    oracleText = "Devour 3 (As this creature enters, you may sacrifice any number of creatures. It enters with three times that many +1/+1 counters on it.)"

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(3))

    replacementEffect(EntersWithDevour(multiplier = 3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Brandon Kitkouski"
        flavorText = "Viashino thrashes are led by elders who have survived countless challenges."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/5552da28-01a3-4277-9e69-b297c3874ea4.jpg"
    }
}
