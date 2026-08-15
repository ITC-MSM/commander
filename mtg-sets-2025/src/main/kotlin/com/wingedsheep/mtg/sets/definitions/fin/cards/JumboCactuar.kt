package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget


/**
 * Jumbo Cactuar
 * {5}{G}{G}
 * Creature — Plant
 * 1/7
 * 10,000 Needles — Whenever this creature attacks, it gets +9999/+0 until end of turn.
 */
val JumboCactuar = card("Jumbo Cactuar") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    oracleText = "10,000 Needles — Whenever this creature attacks, it gets +9999/+0 until end of turn."
    power = 1
    toughness = 7
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(9999, 0, EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "191"
        artist = "Jason Kiantoro"
        flavorText = "Some Cactuars live long lives and grow huge. This Jumbo Cactuar is one of them."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db01c222-8795-47e9-a789-e7f749a3ee7d.jpg"
    }
}
