package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vorosh, the Hunter
 * {3}{B}{G}{U}
 * Legendary Creature — Dragon
 * 6/6
 * Flying
 * Whenever Vorosh deals combat damage to a player, you may pay {2}{G}. If you do, put six +1/+1 counters on Vorosh.
 */
val VoroshTheHunter = card("Vorosh, the Hunter") {
    manaCost = "{3}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Legendary Creature — Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "Whenever Vorosh deals combat damage to a player, you may pay {2}{G}. If you do, put six +1/+1 counters on Vorosh."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{2}{G}"))),
            then = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 6, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "164"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da4438fe-b11b-4adb-b8dd-b44e12ef6124.jpg"
    }
}
