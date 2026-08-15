package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect

/**
 * Saw
 * {2}
 * Artifact — Equipment
 * Equipped creature gets +2/+0.
 * Whenever equipped creature attacks, you may sacrifice a permanent other than that creature or
 * this Equipment. If you do, draw a card.
 * Equip {2}
 *
 * The static buff is the usual [ModifyStats] over [Filters.EquippedCreature]. The attack trigger
 * binds to the attached creature ([TriggerBinding.ATTACHED] on [Triggers.attacks], i.e. "whenever
 * equipped creature attacks"). The "you may sacrifice … If you do, draw" pay-then-payoff is a
 * [GatedEffect] with a [Gate.MayPay] whose cost is a [SacrificeEffect] over permanents you control;
 * `excludeSource = true` removes this Equipment from the choices and `notAttachedToBySource()`
 * removes the equipped creature ("that creature"), so the sacrifice is "a permanent other than that
 * creature or this Equipment". Declining (or controlling no other sacrificeable permanent) skips
 * the draw.
 */
val Saw = card("Saw") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\n" +
        "Whenever equipped creature attacks, you may sacrifice a permanent other than that " +
        "creature or this Equipment. If you do, draw a card.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(+2, 0, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.attacks(binding = TriggerBinding.ATTACHED)
        effect = GatedEffect(
            gate = Gate.MayPay(
                SacrificeEffect(
                    filter = GameObjectFilter.Permanent.notAttachedToBySource(),
                    count = 1,
                    excludeSource = true
                )
            ),
            then = Effects.DrawCards(1)
        )
        description = "Whenever equipped creature attacks, you may sacrifice a permanent other " +
            "than that creature or this Equipment. If you do, draw a card."
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "254"
        artist = "Jarel Threat"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/603c3ef4-4ef1-4db8-9ed2-e2b0926269d5.jpg?1726286820"
    }
}
