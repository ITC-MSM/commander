package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Uglúk of the White Hand
 * {2}{B}{R}
 * Legendary Creature — Orc Soldier
 * 3/3
 *
 * Whenever another creature you control dies, put a +1/+1 counter on Uglúk.
 * If that creature was a Goblin or Orc, put two +1/+1 counters on Uglúk instead.
 *
 * Modeled with two mutually exclusive triggers: a dying creature is either a
 * Goblin/Orc (two counters) or neither (one counter), so exactly one fires.
 */
val UglukOfTheWhiteHand = card("Uglúk of the White Hand") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Orc Soldier"
    power = 3
    toughness = 3
    oracleText = "Whenever another creature you control dies, put a +1/+1 counter on Uglúk. " +
        "If that creature was a Goblin or Orc, put two +1/+1 counters on Uglúk instead."

    // Another non-Goblin, non-Orc creature you control dies -> one +1/+1 counter.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl()
                .notSubtype(com.wingedsheep.sdk.core.Subtype("Goblin"))
                .notSubtype(com.wingedsheep.sdk.core.Subtype("Orc")),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever another creature you control dies, put a +1/+1 counter on Uglúk."
    }

    // Another Goblin or Orc creature you control dies -> two +1/+1 counters.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().withAnySubtype("Goblin", "Orc"),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "If that creature was a Goblin or Orc, put two +1/+1 counters on Uglúk instead."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Bartłomiej Gaweł"
        flavorText = "\"I am Uglúk. I command. I return to Isengard by the shortest road.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e914c7fc-be3c-4346-bf37-6e10f4998204.jpg?1686970114"
    }
}
