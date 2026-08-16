package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Midnight Guard
 * {2}{W}
 * Creature — Human Soldier
 * 2/3
 * Whenever another creature enters, untap this creature.
 *
 * "Another creature" is any creature, any controller — [TriggerBinding.OTHER] over an unscoped
 * [GameObjectFilter.Creature], the same shape as Soul Warden.
 */
val MidnightGuard = card("Midnight Guard") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3
    oracleText = "Whenever another creature enters, untap this creature."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Untap(EffectTarget.Self)
        description = "Whenever another creature enters, untap this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Jason A. Engle"
        flavorText = "\"When you're on watch, no noise is harmless and no shadow can be ignored.\" —Olgard of the Skiltfolk"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2264b760-c527-470d-bad0-d8baaf543631.jpg?1783940853"
    }
}
