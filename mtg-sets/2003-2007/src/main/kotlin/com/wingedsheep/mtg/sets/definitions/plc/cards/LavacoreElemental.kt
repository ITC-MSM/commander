package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lavacore Elemental
 * {2}{R}
 * Creature — Elemental
 * 5/3
 * Vanishing 1
 * Whenever a creature you control deals combat damage to a player, put a time counter on this creature.
 *
 * The counter goes on **Lavacore**, not on the creature that connected, so this is one observer
 * trigger on Lavacore with a `sourceFilter` — *not* a `GrantTriggeredAbility` to your creatures,
 * where `EffectTarget.Self` would resolve to the grantee and put the counter on the wrong
 * permanent. Vanishing 1 makes the distinction load-bearing: the wrong reading never refuels it.
 */
val LavacoreElemental = card("Lavacore Elemental") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 5
    toughness = 3
    oracleText = "Vanishing 1 (This creature enters with a time counter on it. At the beginning of your upkeep, remove a time counter from it. When the last is removed, sacrifice it.)\n" +
        "Whenever a creature you control deals combat damage to a player, put a time counter on this creature."

    keywordAbility(KeywordAbility.vanishing(1))

    triggeredAbility {
        trigger = TriggerSpec(
            event = EventPattern.DealsDamageEvent(
                damageType = DamageType.Combat,
                recipient = RecipientFilter.AnyPlayer,
                sourceFilter = GameObjectFilter.Creature.youControl()
            ),
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.TIME, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "103"
        artist = "E. M. Gist"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6b2f4de-fc41-4e68-8655-577616b10c7e.jpg"
    }
}
