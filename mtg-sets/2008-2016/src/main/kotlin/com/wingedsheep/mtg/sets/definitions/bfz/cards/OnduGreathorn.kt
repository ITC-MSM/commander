package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ondu Greathorn
 * {3}{W}
 * Creature — Beast
 * 2/3
 * First strike
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val OnduGreathorn = card("Ondu Greathorn") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 3
    oracleText = "First strike\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Aaron Miller"
        flavorText = "\"May your horns get lodged in an Eldrazi's bony face, ornery brute!\"\n" +
            "—Bruse Tarl, Goma Fada nomad"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95d9668e-05dc-41c4-9326-ef4c0e15dd80.jpg?1783938217"
    }
}
