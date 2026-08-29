package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Charging Griffin
 * {3}{W}
 * Creature — Griffin
 * 2/2
 *
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * Whenever this creature attacks, it gets +1/+1 until end of turn.
 */
val ChargingGriffin = card("Charging Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "Whenever this creature attacks, it gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Erica Yang"
        flavorText = "Four claws, two wings, one beak, no fear."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88637cc0-3b2a-402c-b491-26fcc2d21fb8.jpg"
    }
}
