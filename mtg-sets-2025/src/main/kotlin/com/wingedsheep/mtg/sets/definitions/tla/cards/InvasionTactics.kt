package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.OneOrMoreDealCombatDamageToPlayerEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Invasion Tactics
 * {4}{G}
 * Enchantment
 *
 * When this enchantment enters, creatures you control get +2/+2 until end of turn.
 * Whenever one or more Allies you control deal combat damage to a player, draw a card.
 *
 * The combat-damage clause is a batched offensive trigger
 * ([OneOrMoreDealCombatDamageToPlayerEvent], `TriggerBinding.ANY`): it fires at most once per
 * combat-damage batch no matter how many Allies connected, so a swarm draws exactly one card —
 * a per-source `dealsDamage` trigger would over-draw.
 */
val InvasionTactics = card("Invasion Tactics") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, creatures you control get +2/+2 until end of turn.\n" +
        "Whenever one or more Allies you control deal combat damage to a player, draw a card."

    // When this enchantment enters, creatures you control get +2/+2 until end of turn.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.modifyStatsForAll(2, 2, GroupFilter.AllCreaturesYouControl)
    }

    // Whenever one or more Allies you control deal combat damage to a player, draw a card.
    triggeredAbility {
        trigger = TriggerSpec(
            OneOrMoreDealCombatDamageToPlayerEvent(
                sourceFilter = GameObjectFilter.Creature.withSubtype("Ally"),
            ),
            TriggerBinding.ANY,
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Eduardo Francisco"
        flavorText = "\"Your moment of truth isn't gonna be in front of some map. " +
            "It's gonna to be out there on the battlefield.\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27c6330d-49f6-4707-b4d4-d1411fa422eb.jpg?1764121242"
    }
}
