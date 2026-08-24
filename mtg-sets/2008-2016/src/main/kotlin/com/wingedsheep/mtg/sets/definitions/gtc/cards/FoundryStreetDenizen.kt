package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Foundry Street Denizen
 * {R}
 * Creature — Goblin Warrior
 * 1/1
 * Whenever another red creature you control enters, this creature gets +1/+0 until end of turn.
 *
 * "**Another**" is [TriggerBinding.OTHER] — the Denizen's own arrival does not pump it.
 *
 * Canonical printing: Gatecrash, the card's earliest real-expansion printing. Reprinted in M15 as
 * a `Printing` row.
 */
val FoundryStreetDenizen = card("Foundry Street Denizen") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 1
    oracleText = "Whenever another red creature you control enters, this creature gets +1/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withColor(Color.RED).youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "Whenever another red creature you control enters, this creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Raoul Vitale"
        flavorText = "After the Foundry Street riot, Arrester Hulbein tried to ban bludgeons. Which, inevitably, resulted in another riot."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0befed63-07ba-4728-9078-57bbccbeeeb1.jpg?1783940125"
    }
}
