package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Blood Artist
 * {1}{B}
 * Creature — Vampire
 * 0/1
 * Whenever this creature or another creature dies, target player loses 1 life and you gain 1 life.
 */
val BloodArtist = card("Blood Artist") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 0
    toughness = 1
    oracleText = "Whenever this creature or another creature dies, target player loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature,
                from = Zone.BATTLEFIELD,
                to = Zone.GRAVEYARD
            ),
            binding = TriggerBinding.ANY
        )
        val player = target("player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                Effects.LoseLife(1, player),
                Effects.GainLife(1)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e1fb442-68ff-4249-8e44-87edf6fae211.jpg?1782714502"
    }
}
