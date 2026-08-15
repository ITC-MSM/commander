package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Seeker of the Way
 * {1}{W}
 * Creature — Human Warrior
 * 2/2
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Whenever you cast a noncreature spell, Seeker of the Way gains lifelink until end of turn.
 */
val SeekerOfTheWay = card("Seeker of the Way") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\nWhenever you cast a noncreature spell, Seeker of the Way gains lifelink until end of turn."

    prowess()

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Craig J Spearing"
        flavorText = "\"I don't know where my destiny lies, but I know it isn't here.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c17e350-44f7-4413-ad24-7c5d6616effd.jpg?1562785152"
    }
}
