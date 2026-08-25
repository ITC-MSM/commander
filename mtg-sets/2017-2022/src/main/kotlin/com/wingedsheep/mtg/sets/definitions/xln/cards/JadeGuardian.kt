package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Jade Guardian
 * {3}{G}
 * Creature — Merfolk Shaman
 * 2/2
 *
 * Hexproof
 * When this creature enters, put a +1/+1 counter on target Merfolk you control.
 */
val JadeGuardian = card("Jade Guardian") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Shaman"
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your " +
        "opponents control.)\n" +
        "When this creature enters, put a +1/+1 counter on target Merfolk you control."
    power = 2
    toughness = 2

    keywords(Keyword.HEXPROOF)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val merfolk = target(
            "target",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl())
            )
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, merfolk)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Chris Seaman"
        flavorText = "The River Heralds believe that jade gives weight to their magic."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/aca83e48-6e32-477f-8714-6103e77c06df.jpg"
    }
}
