package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lurking Nightstalker
 * {B}{B}
 * Creature — Nightstalker
 *
 * "It gets +2/+0" is the source itself, so the pump is aimed at [EffectTarget.Self] rather than at
 * a target slot the ability never announces.
 */
val LurkingNightstalker = card("Lurking Nightstalker") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    oracleText = "Whenever this creature attacks, it gets +2/+0 until end of turn."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Kev Walker"
        flavorText = "The shadows know."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/002715a3-b84f-40ba-8fa9-6b2854626f4d.jpg"
    }
}
