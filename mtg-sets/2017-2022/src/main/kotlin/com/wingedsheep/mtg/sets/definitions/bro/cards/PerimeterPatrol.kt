package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Perimeter Patrol
 * {2}{G}
 * Creature — Human Soldier
 * 3/3
 * Whenever an artifact you control enters, this creature gets +1/+0 until end of turn.
 *
 * The Weldfast Wingsmith trigger shape — [Triggers.entersBattlefield] over
 * `Artifact.youControl()` with [TriggerBinding.ANY] — feeding a self-targeted
 * [Effects.ModifyStats] (default `Duration.EndOfTurn`).
 */
val PerimeterPatrol = card("Perimeter Patrol") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 3
    oracleText = "Whenever an artifact you control enters, this creature gets +1/+0 until end of turn."

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
        collectorNumber = "188"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "The Fallaji quickly learned that the Kher Ridges held too many caves and valleys to be surveyed from the air alone."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4719bc1-4c27-45d8-89f7-8c76ccf946d2.jpg?1783920042"
    }
}
