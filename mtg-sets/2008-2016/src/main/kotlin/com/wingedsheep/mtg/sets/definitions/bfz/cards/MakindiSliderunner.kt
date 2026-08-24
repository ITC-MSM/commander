package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Makindi Sliderunner
 * {1}{R}
 * Creature — Beast
 * 2/1
 * Trample
 * Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val MakindiSliderunner = card("Makindi Sliderunner") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 1
    oracleText = "Trample\n" +
        "Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Matt Stewart"
        flavorText = "After a battle, it breaks the hillsides into manageable pieces to prepare for next time."
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e6da400-ee4e-44d1-887d-1e2fb59b9322.jpg?1783938193"
    }
}
