package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Phalanx Vanguard
 * {1}{W}
 * Creature — Human Soldier
 * 2/2
 * Vigilance
 * Whenever an artifact you control enters, this creature gets +1/+0 until end of turn.
 *
 * Perimeter Patrol's trigger in white with vigilance printed above it — the shared BRO
 * "artifact you control enters" shape: [Triggers.entersBattlefield] over `Artifact.youControl()`
 * with [TriggerBinding.ANY], feeding a self-targeted [Effects.ModifyStats].
 */
val PhalanxVanguard = card("Phalanx Vanguard") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Vigilance\nWhenever an artifact you control enters, this creature gets +1/+0 until end of turn."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Izzy"
        flavorText = "The cool, sleek automatons beside her mirrored her own iron resolve."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2abe2af7-deba-4569-822a-2d9309aeaadd.jpg?1783920125"
    }
}
