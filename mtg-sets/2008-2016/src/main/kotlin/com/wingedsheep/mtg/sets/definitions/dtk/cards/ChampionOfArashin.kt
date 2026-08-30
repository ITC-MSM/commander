package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Champion of Arashin
 * {3}{W}
 * Creature — Dog Warrior
 * 3 / 2
 *
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 *
 * A single evergreen keyword and nothing else, so the whole card is one `keywords(...)` line —
 * the reminder text is printed flavour that the engine reads straight off `Keyword.LIFELINK`.
 */
val ChampionOfArashin = card("Champion of Arashin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog Warrior"
    power = 3
    toughness = 2
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Joseph Meehan"
        flavorText = "\"The blood of Dromoka and the blood of my veins are the same.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/6/5635c8f2-164c-4a13-965a-9432d07092ed.jpg?1783938619"
    }
}
