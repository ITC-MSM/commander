package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shatterskull Recruit
 * {3}{R}{R}
 * Creature — Giant Warrior Ally
 * 4/4
 * Menace (This creature can't be blocked except by two or more creatures.)
 */
val ShatterskullRecruit = card("Shatterskull Recruit") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior Ally"
    power = 4
    toughness = 4
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)"

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "David Palumbo"
        flavorText = "Saved from certain death by the kor, he considers himself bound to them by an unbreakable " +
            "blood oath."
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8add5f2-4ccf-4505-86f6-cc36aff1c3fe.jpg?1783938192"
    }
}
