package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Judge of Currents
 * {1}{W}
 * Creature — Merfolk Wizard
 * 1/1
 * Whenever a Merfolk you control becomes tapped, you may gain 1 life.
 *
 * "a Merfolk you control" covers Judge of Currents itself, so this is the ANY binding with a
 * filter rather than [Triggers.BecomesTapped]'s SELF one. It is per-permanent, not batched —
 * attacking with three Merfolk gains three separate triggers.
 */
val JudgeOfCurrents = card("Judge of Currents") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "Whenever a Merfolk you control becomes tapped, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl()
        )
        effect = MayEffect(Effects.GainLife(1))
        description = "Whenever a Merfolk you control becomes tapped, you may gain 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Dan Murayama Scott"
        flavorText = "Though the currents of the Lanes shift every year, the merrow never lose track of where they are or where they are going."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/3073f3f5-bff8-4ec1-a68f-c83d63435843.jpg?1783942913"
    }
}
