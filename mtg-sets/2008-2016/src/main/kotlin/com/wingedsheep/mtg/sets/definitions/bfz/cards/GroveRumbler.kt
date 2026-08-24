package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grove Rumbler
 * {2}{R}{G}
 * Creature — Elemental
 * 3/3
 * Trample
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val GroveRumbler = card("Grove Rumbler") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Greg Opalinski"
        flavorText = "\"The land will not wait for the enemy to arrive.\"\n" +
            "—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/012b8eff-cdf3-423e-ae17-72a909e7ebd3.jpg?1783938181"
    }
}
