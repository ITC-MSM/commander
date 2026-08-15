package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crossroads Watcher
 * {2}{G}
 * Creature — Kithkin Ranger
 * 3/3
 *
 * Trample
 * Whenever another creature you control enters, this creature gets +1/+0 until end of turn.
 */
val CrossroadsWatcher = card("Crossroads Watcher") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kithkin Ranger"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "Whenever another creature you control enters, this creature gets +1/+0 until end of turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = ModifyStatsEffect(1, 0, EffectTarget.Self, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Aurore Folny"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d62fcbb-f1a0-46ce-a4af-2a33bcc3ac8e.jpg?1767658374"
    }
}
