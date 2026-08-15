package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Underwater Tunnel // Slimy Aquarium (DSK 79) — split-layout Room (CR 709.5).
 *
 * Underwater Tunnel {U} — Enchantment — Room
 *   When you unlock this door, surveil 2.
 *
 * Slimy Aquarium {3}{U} — Enchantment — Room
 *   When you unlock this door, manifest dread, then put a +1/+1 counter on that creature.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Slimy Aquarium reuses the shared [Patterns.Library.manifestDread] recipe, which stores the
 * manifested creature under the pipeline collection "manifestDreadManifested"; the follow-up
 * +1/+1 counter targets that creature via [EffectTarget.PipelineTarget]. If the library is empty
 * (no creature manifested), there is nothing to put a counter on and that step is a no-op.
 */
val UnderwaterTunnelSlimyAquarium = card("Underwater Tunnel // Slimy Aquarium") {
    layout = CardLayout.SPLIT
    colorIdentity = "U"

    face("Underwater Tunnel") {
        manaCost = "{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, surveil 2."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Surveil(2)
            description = "When you unlock this door, surveil 2."
        }
    }

    face("Slimy Aquarium") {
        manaCost = "{3}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, manifest dread, then put a +1/+1 counter on that creature."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Composite(
                Patterns.Library.manifestDread(),
                Effects.AddCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    1,
                    EffectTarget.PipelineTarget("manifestDreadManifested")
                )
            )
            description = "When you unlock this door, manifest dread, then put a +1/+1 counter on that creature."
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2dd69f2d-8c9c-41fc-93ea-48fc7a6d5272.jpg?1726780798"
    }
}
