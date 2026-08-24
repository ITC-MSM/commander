package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wave-Wing Elemental
 * {5}{U}
 * Creature — Elemental
 * 3/4
 * Flying
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val WaveWingElemental = card("Wave-Wing Elemental") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "John Severin Brassell"
        flavorText = "\"Do you see? All of Tazeem strains at its tether.\"\n" +
            "—Noyan Dar, Tazeem roilmage"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11b45809-87fc-409d-942a-1f31f68d27ac.jpg?1783938206"
    }
}
