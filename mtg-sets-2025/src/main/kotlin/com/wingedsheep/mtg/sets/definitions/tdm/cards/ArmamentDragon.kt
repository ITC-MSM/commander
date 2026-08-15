package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Armament Dragon — Tarkir: Dragonstorm #168
 * {3}{W}{B}{G} · Creature — Dragon · 3/4
 *
 * Flying
 * When this creature enters, distribute three +1/+1 counters among one, two, or three target
 * creatures you control.
 *
 * Same ETB shape as Armament Corps, scaled to three counters across up to three controlled
 * creatures: the [TargetCreature] requirement enforces "one, two, or three target creatures you
 * control" (minCount = 1, count = 3) and [Effects.DistributeCountersAmongTargets] presents the
 * allocation decision over the chosen targets.
 */
val ArmamentDragon = card("Armament Dragon") {
    manaCost = "{3}{W}{B}{G}"
    colorIdentity = "WBG"
    typeLine = "Creature — Dragon"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, distribute three +1/+1 counters among one, two, or three target creatures you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetCreature(count = 3, minCount = 1, filter = TargetFilter.CreatureYouControl)
        effect = Effects.DistributeCountersAmongTargets(totalCounters = 3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17f61c01-0a41-4fa1-ac34-ffa83baad989.jpg?1743204643"
    }
}
