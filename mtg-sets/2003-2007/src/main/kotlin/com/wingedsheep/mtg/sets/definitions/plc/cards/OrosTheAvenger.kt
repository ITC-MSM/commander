package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oros, the Avenger
 * {3}{R}{W}{B}
 * Legendary Creature — Dragon
 * 6/6
 * Flying
 * Whenever Oros deals combat damage to a player, you may pay {2}{W}. If you do, Oros deals 3 damage to each nonwhite creature.
 *
 * "Each nonwhite creature" is untargeted, so the damage is [Effects.ForEachInGroup] over the
 * snapshotted group — Oros itself is red/white/black and so takes the 3 too.
 */
val OrosTheAvenger = card("Oros, the Avenger") {
    manaCost = "{3}{R}{W}{B}"
    colorIdentity = "RWB"
    typeLine = "Legendary Creature — Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "Whenever Oros deals combat damage to a player, you may pay {2}{W}. If you do, Oros deals 3 damage to each nonwhite creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{2}{W}"))),
            then = Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.notColor(Color.WHITE)),
                Effects.DealDamage(3, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "161"
        artist = "Daren Bader"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2790685-f6ae-4106-ae4d-fe97954bcb82.jpg"
    }
}
