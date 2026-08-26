package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Numot, the Devastator
 * {3}{U}{R}{W}
 * Legendary Creature — Dragon
 * 6/6
 * Flying
 * Whenever Numot deals combat damage to a player, you may pay {2}{R}. If you do, destroy up to two target lands.
 *
 * One of the three Planar Chaos "Primeval" dragons. "Up to two target" is a
 * [TargetObject] with `count = 2, optional = true`, and the destroy runs once per chosen target.
 */
val NumotTheDevastator = card("Numot, the Devastator") {
    manaCost = "{3}{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Legendary Creature — Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "Whenever Numot deals combat damage to a player, you may pay {2}{R}. If you do, destroy up to two target lands."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        target = TargetObject(
            count = 2,
            optional = true,
            filter = TargetFilter.Land,
            id = "target"
        )
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{2}{R}"))),
            then = ForEachTargetEffect(
                listOf(
                    Effects.Move(EffectTarget.ContextTarget(0), Zone.GRAVEYARD, byDestruction = true)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Dan Dos Santos"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1bf777a-a232-4504-afbb-13d2f79b4355.jpg"
    }
}
