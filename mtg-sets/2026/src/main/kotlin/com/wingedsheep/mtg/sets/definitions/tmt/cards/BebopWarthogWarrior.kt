package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bebop, Warthog Warrior
 * {4}{B}
 * Legendary Creature — Boar Mutant Warrior
 * 5/4
 *
 * Menace
 * Rhinos you control have menace.
 * Swampcycling {2}
 */
val BebopWarthogWarrior = card("Bebop, Warthog Warrior") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Boar Mutant Warrior"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\nRhinos you control have menace.\nSwampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it, put it into your hand, then shuffle.)"
    power = 5
    toughness = 4

    keywords(Keyword.MENACE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.MENACE,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype("Rhino").youControl()
            )
        )
    }

    keywordAbility(KeywordAbility.typecycling("Swamp", "{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "April Prime"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/371ba16d-73f7-450c-8b1f-c05012a4ca93.jpg?1771586838"
    }
}
