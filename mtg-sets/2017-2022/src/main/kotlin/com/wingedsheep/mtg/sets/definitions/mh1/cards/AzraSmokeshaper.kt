package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Azra Smokeshaper
 * {3}{B}
 * Creature — Azra Ninja
 * 3/3
 * Ninjutsu {1}{B} ({1}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)
 * When this creature enters, target creature you control gains indestructible until end of turn.
 */
val AzraSmokeshaper = card("Azra Smokeshaper") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Azra Ninja"
    power = 3
    toughness = 3
    oracleText = "Ninjutsu {1}{B} ({1}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "When this creature enters, target creature you control gains indestructible until end of turn."

    ninjutsu("{1}{B}")

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Aaron Miller"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f627e521-2b8f-4d0f-a9d3-cc4e331ce57d.jpg?1783933132"
    }
}
