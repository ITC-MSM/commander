package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rotcrown Ghoul — Avacyn Restored #72
 * {4}{U} · Creature — Zombie · 3/3
 *
 * When this creature dies, target player mills five cards.
 *
 * The mill is the [Patterns.Library] gather → move pipeline pointed at the chosen player
 * ([EffectTarget.ContextTarget]), not at the controller.
 */
val RotcrownGhoul = card("Rotcrown Ghoul") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 3
    oracleText = "When this creature dies, target player mills five cards."

    triggeredAbility {
        trigger = Triggers.Dies
        target("target", Targets.Player)
        effect = Patterns.Library.mill(5, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Dave Kendall"
        flavorText = "\"Don't look into its eyes. It's thinking things no dead thing should think.\"\n—Captain Eberhart"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f13b5ba6-0de1-4f5c-867b-57e2c10bde8e.jpg?1783940711"
    }
}
