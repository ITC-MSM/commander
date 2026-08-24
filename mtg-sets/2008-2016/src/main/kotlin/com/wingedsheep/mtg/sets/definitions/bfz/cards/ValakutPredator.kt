package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Valakut Predator
 * {2}{R}
 * Creature — Elemental
 * 2/2
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val ValakutPredator = card("Valakut Predator") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 2
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Kev Walker"
        flavorText = "\"Whatever volcanoes dream of, it seems like they always wake up grumpy.\"\n" +
            "—Raff Slugeater, goblin shortcutter"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88318272-8192-4d6d-a22a-eca87abb480d.jpg?1783938191"
    }
}
