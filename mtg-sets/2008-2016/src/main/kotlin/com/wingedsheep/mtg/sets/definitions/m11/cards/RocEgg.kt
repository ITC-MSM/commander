package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Roc Egg
 * {2}{W}
 * Creature — Bird Egg
 * 0/3
 *
 * Defender (This creature can't attack.)
 * When this creature dies, create a 3/3 white Bird creature token with flying.
 *
 * - [Triggers.Dies] is the battlefield-to-graveyard zone change bound to the source itself
 *   (CR 700.4), so the egg hatches whether it was destroyed, sacrificed, or died to lethal damage —
 *   but not when it is exiled or bounced.
 * - The reminder text for defender is printed on the card and kept verbatim in the oracle text.
 */
val RocEgg = card("Roc Egg") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Egg"
    power = 0
    toughness = 3
    oracleText = "Defender (This creature can't attack.)\n" +
        "When this creature dies, create a 3/3 white Bird creature token with flying."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Bird"),
            keywords = setOf(Keyword.FLYING)
        )
        description = "When this creature dies, create a 3/3 white Bird creature token with flying."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Paul Bonner"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1dca2c1f-3835-478b-860c-51b2036221b2.jpg?1783941833"
    }
}
