package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Backup Agent
 * {1}{W}
 * Creature — Human Citizen
 * 1 / 1
 * When this creature enters, put a +1/+1 counter on target creature.
 *
 * The target is unrestricted ("target creature", not "creature you control"), so it is the bare
 * [Targets.Creature] requirement.
 */
val BackupAgent = card("Backup Agent") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    oracleText = "When this creature enters, put a +1/+1 counter on target creature."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
        description = "When this creature enters, put a +1/+1 counter on target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Aaron J. Riley"
        flavorText = "\"My sources say the Beamtown Bullies were spotted in the Mezzio. Keep your eyes sharp and stay close to me.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a46af75-3880-4141-b26e-19834d67e7a8.jpg?1783923164"
    }
}
