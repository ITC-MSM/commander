package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Super Shredder
 * {1}{B}
 * Legendary Creature — Mutant Ninja Human
 * 1/1
 *
 * Menace
 * Whenever another permanent leaves the battlefield, put a +1/+1
 * counter on Super Shredder.
 */
val SuperShredder = card("Super Shredder") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Mutant Ninja Human"
    oracleText = "Menace\nWhenever another permanent leaves the battlefield, put a +1/+1 counter on Super Shredder."
    power = 1
    toughness = 1

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Permanent,
                from = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever another permanent leaves the battlefield, put a +1/+1 counter on Super Shredder."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "83"
        artist = "Néstor Ossandón Leal"
        flavorText = "\"I have molded myself into perfection, Hamato. Look upon me, and know fear.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37a497b8-e908-4ddc-996e-a8470df72afb.jpg?1760102707"
    }
}
