package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Prowling Felidar
 * {3}{W}
 * Creature — Cat Beast
 * 2/3
 * Vigilance
 * Landfall — Whenever a land you control enters, put a +1/+1 counter on this creature.
 */
val ProwlingFelidar = card("Prowling Felidar") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Beast"
    power = 2
    toughness = 3
    oracleText = "Vigilance\nLandfall — Whenever a land you control enters, put a +1/+1 counter on this creature."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Landfall — Whenever a land you control enters, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Ilse Gort"
        flavorText = "On occasion, felidars leave meaty \"gifts\" outside the tents of explorers they consider worthy."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9d1c11a-a32c-449c-95c6-450dce6c26d2.jpg?1783929408"

        ruling(
            "2024-11-08",
            "A landfall ability triggers whenever a land you control enters for any reason. It " +
                "triggers whenever you play a land, as well as whenever a spell or ability puts a " +
                "land onto the battlefield under your control."
        )
        ruling(
            "2024-11-08",
            "A landfall ability doesn't trigger if a permanent already on the battlefield becomes a land."
        )
        ruling(
            "2024-11-08",
            "Whenever a land you control enters, each landfall ability of the permanents you control " +
                "will trigger. You can put them on the stack in any order. The last ability you put " +
                "on the stack will be the first one to resolve (As a result, you can have those " +
                "abilities resolve in the order of your choosing.)."
        )
    }
}
