package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Ruric Thar, Magecrusher — Karakyk Guardian's hexproof clause narrowed to *combat* damage. "They"
 * is Ruric Thar (both heads). Combat damage to anything counts — a player, a blocking or blocked
 * creature, a planeswalker, a battle — while noncombat damage (a fight) does not. "Yet" is this
 * object's time on the battlefield: the marker is dropped on a zone change, so a Ruric Thar that
 * leaves and comes back has hexproof again (CR 400.7).
 */
val RuricTharMagecrusher = card("Ruric Thar, Magecrusher") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Ogre Warrior"
    power = 7
    toughness = 7
    oracleText = "This spell can't be countered.\n" +
        "Reach, vigilance, trample\n" +
        "Ruric Thar has hexproof as long as they haven't dealt combat damage yet. " +
        "(They can't be the target of spells or abilities your opponents control.)"

    cantBeCountered = true

    keywords(Keyword.REACH, Keyword.VIGILANCE, Keyword.TRAMPLE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HEXPROOF, Filters.Self),
            condition = Conditions.Not(Conditions.SourceHasDealtCombatDamage)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "265"
        artist = "Olivier Bernard"
        flavorText = "\"Can't cast spells with a broken mouth!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eed83302-dc2c-45f4-a4bd-af9da51edef5.jpg?1789358701"
        inBooster = false
    }
}
