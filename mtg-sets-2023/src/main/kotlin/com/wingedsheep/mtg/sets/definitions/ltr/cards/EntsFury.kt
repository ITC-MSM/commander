package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Ent's Fury
 * {1}{G}
 * Sorcery
 *
 * Put a +1/+1 counter on target creature you control if its power is 4 or greater.
 * Then that creature gets +1/+1 until end of turn and fights target creature you don't control.
 */
val EntsFury = card("Ent's Fury") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on target creature you control if its power is 4 or greater. Then that creature gets +1/+1 until end of turn and fights target creature you don't control."

    spell {
        val mine = target("creature you control", Targets.CreatureYouControl)
        val theirs = target("creature you don't control", Targets.CreatureOpponentControls)
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                filter = GameObjectFilter.Creature.powerAtLeast(4),
                targetIndex = 0
            ),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, mine)
        )
            .then(Effects.ModifyStats(1, 1, mine))
            .then(Effects.Fight(mine, theirs))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Hristo D. Chukov"
        flavorText = "\"I came and called the trees by their long names, but they did not hear or answer—they lay dead.\"\n—Quickbeam"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7946af6-69ab-47a1-955c-1954a04752df.jpg?1686969344"
    }
}
