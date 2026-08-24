package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thalakos Sentry
 * {1}{U}
 * Creature — Thalakos Soldier
 * 1/2
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 */
val ThalakosSentry = card("Thalakos Sentry") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Thalakos Soldier"
    power = 1
    toughness = 2
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)"

    keywords(Keyword.SHADOW)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Andrew Robinson"
        flavorText = "\"Ill luck and poor geography caught the Thalakos between us and the Dauthi.\"\n" +
            "—Lyna, Soltari emissary"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/739a13d6-5f73-4166-b923-9db8ee3f2cf7.jpg"
    }
}
