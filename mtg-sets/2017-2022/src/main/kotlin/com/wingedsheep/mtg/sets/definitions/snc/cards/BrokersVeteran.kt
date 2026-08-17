package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brokers Veteran
 * {1}{U}
 * Creature — Human Soldier
 * 2 / 1
 * When this creature dies, put a shield counter on target creature you control. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)
 *
 * The shield counter is the engine's CR 122.1c counter — the reminder text's replacement +
 * prevention pair is wired at the damage and destroy chokepoints, so the card only has to put
 * the counter on.
 */
val BrokersVeteran = card("Brokers Veteran") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier"
    oracleText = "When this creature dies, put a shield counter on target creature you control. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)"
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.SHIELD, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Lie Setiawan"
        flavorText = "One last job before he retires."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d4a54bd-5598-4313-b5e6-29fd38da016a.jpg?1783923149"
    }
}
