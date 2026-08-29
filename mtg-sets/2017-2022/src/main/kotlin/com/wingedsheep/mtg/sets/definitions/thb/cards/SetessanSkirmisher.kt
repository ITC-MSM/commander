package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Setessan Skirmisher
 * {1}{G}
 * Creature — Human Warrior
 * 2/1
 *
 * Constellation — Whenever an enchantment you control enters, this creature gets +1/+1 until end of turn.
 *
 * Constellation is an ability word with no rules meaning of its own — a plain enters-the-battlefield
 * watcher over enchantments you control, bound with [TriggerBinding.ANY] because the Skirmisher is
 * not the permanent being watched. The pump is untargeted: it always hits the source itself, so it
 * is [EffectTarget.Self] rather than a target slot.
 */
val SetessanSkirmisher = card("Setessan Skirmisher") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 1
    oracleText = "Constellation — Whenever an enchantment you control enters, this creature gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Constellation — Whenever an enchantment you control enters, this creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Greg Staples"
        flavorText = "Bassara, the tower of the fox, houses the elite skirmishers who guard the Nessian Wood against trespassers."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46739993-6afe-428d-bf63-b57649e38a65.jpg"
    }
}
