package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Avenging Angel
 * {3}{W}{W}
 * Creature — Angel
 * 3/3
 * Flying
 * When this creature dies, you may put it on top of its owner's library.
 */
val AvengingAngel = card("Avenging Angel") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature dies, you may put it on top of its owner's library."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        effect = Effects.PutOnTopOfLibrary(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28333138-60bc-459b-a0cd-1b7fd19c89cd.jpg"
    }
}
