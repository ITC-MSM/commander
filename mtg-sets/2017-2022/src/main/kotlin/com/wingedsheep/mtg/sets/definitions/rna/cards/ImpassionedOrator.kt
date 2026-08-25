package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Impassioned Orator — Ravnica Allegiance #12
 * {1}{W} · Creature — Human Cleric · 2 / 2
 *
 * "Whenever **another** creature you control enters" is [TriggerBinding.OTHER] over the
 * creatures-you-control filter — the Orator's own arrival must not trigger it.
 */
val ImpassionedOrator = card("Impassioned Orator") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "Whenever another creature you control enters, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Mark Zug"
        flavorText = "In times of unrest, the crowd is eager for the comfort of strong convictions."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bd746e3-8934-4c86-894e-2cb1738b1d58.jpg"
    }
}
