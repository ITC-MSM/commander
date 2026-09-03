package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sky Theater Strix
 * {1}{U}
 * Creature — Bird
 * 1/2
 * Flying
 * Whenever you cast a noncreature spell, this creature gets +1/+0 until end of turn.
 */
val SkyTheaterStrix = card("Sky Theater Strix") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, this creature gets +1/+0 until end of turn."
    power = 1
    toughness = 2
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Chris Seaman"
        flavorText = "Courier owls joined the fray, attacking the Dreadhorde with a viciousness usually reserved for mail thieves."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98902dd9-f21c-4419-8205-4b9d6592bf28.jpg"
    }
}
