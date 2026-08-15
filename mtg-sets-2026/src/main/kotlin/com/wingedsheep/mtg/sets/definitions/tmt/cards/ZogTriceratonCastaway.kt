package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Zog, Triceraton Castaway
 * {4}{R}
 * Legendary Creature — Dinosaur Soldier
 * 5/4
 *
 * Reach, trample
 * When Zog enters, target creature can't block this turn.
 * Mountaincycling {2}
 */
val ZogTriceratonCastaway = card("Zog, Triceraton Castaway") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dinosaur Soldier"
    oracleText = "Reach, trample\nWhen Zog enters, target creature can't block this turn.\nMountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card, reveal it, put it into your hand, then shuffle.)"
    power = 5
    toughness = 4

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.CantBlock(creature)
    }

    keywordAbility(KeywordAbility.typecycling("Mountain", "{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a83a70ba-448e-45f3-b23b-6f38af54811f.jpg?1771502686"
    }
}
