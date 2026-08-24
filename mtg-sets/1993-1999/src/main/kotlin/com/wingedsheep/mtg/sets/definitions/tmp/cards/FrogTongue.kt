package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Frog Tongue
 * {G}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature has reach. (It can block creatures with flying.)
 */
val FrogTongue = card("Frog Tongue") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature has reach. (It can block creatures with flying.)"

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.REACH)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "228"
        artist = "Phil Foglio"
        flavorText = "\"But *why* can't I get one?\" sniveled Squee. \"All da bugs here got wings.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3941e799-a254-423e-90bb-091dbe56ca6a.jpg"
    }
}
