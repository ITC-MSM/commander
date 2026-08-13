package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Khenra Eternal
 * {1}{B}
 * Creature — Zombie Jackal Warrior
 * 2/2
 * Afflict 1 (Whenever this creature becomes blocked, defending player loses 1 life.)
 */
val KhenraEternal = card("Khenra Eternal") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Jackal Warrior"
    power = 2
    toughness = 2
    oracleText = "Afflict 1 (Whenever this creature becomes blocked, defending player loses 1 life.)"

    keywordAbility(KeywordAbility.Numeric(Keyword.AFFLICT, 1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Tomasz Jedruszek"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abbdc277-4a76-44a9-aeda-edabab1f579e.jpg?1783936040"
    }
}
