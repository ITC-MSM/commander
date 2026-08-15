package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cosmic Spider-Man
 * {W}{U}{B}{R}{G}
 * Legendary Creature — Spider Human Hero
 * 5/5
 * Flying, first strike, trample, lifelink, haste
 * At the beginning of combat on your turn, other Spiders you control gain flying, first strike, trample, lifelink, and haste until end of turn.
 */
val CosmicSpiderMan = card("Cosmic Spider-Man") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Spider Human Hero"
    oracleText = "Flying, first strike, trample, lifelink, haste\nAt the beginning of combat on your turn, other Spiders you control gain flying, first strike, trample, lifelink, and haste until end of turn."
    power = 5
    toughness = 5
    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.TRAMPLE, Keyword.LIFELINK, Keyword.HASTE)
    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SPIDER).youControl(), excludeSelf = true),
            Effects.Composite(
                Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
            )
        )
    }
    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "127"
        artist = "Zoltan Boros"
        flavorText = "The Enigma Force granted Peter the greatest power, and with it, the greatest responsibility."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f82f4013-7308-4917-9042-19a5909f2134.jpg?1757377651"
    }
}
