package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spiderwig Boggart
 * {2}{B}
 * Creature — Goblin Shaman
 * 2/2
 * When this creature enters, target creature gains fear until end of turn.
 */
val SpiderwigBoggart = card("Spiderwig Boggart") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, target creature gains fear until end of turn. (It " +
        "can't be blocked except by artifact creatures and/or black creatures.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FEAR, creature)
        description = "target creature gains fear until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Larry MacDougall"
        flavorText = "Auntie Flint was the first to pioneer the spiderwig, a mass of arachnids intended to be worn rather than eaten."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88e77c10-b569-485f-ba3e-16ef0c57dd81.jpg?1783942883"
    }
}
