package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cathar's Companion
 * {2}{W}
 * Creature — Dog
 * 3/1
 *
 * Whenever you cast a noncreature spell, this creature gains indestructible until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 */
val CatharsCompanion = card("Cathar's Companion") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    oracleText = "Whenever you cast a noncreature spell, this creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
        description = "Whenever you cast a noncreature spell, this creature gains indestructible until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Svetlin Velinov"
        flavorText = "\"Unwavering and loyal, they represent stability in uncertain times.\"\n—Rem Karolus, Slayer of Angels"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f4b9943-0fe6-4383-90a0-4a719dcf9499.jpg?1783937824"
    }
}
