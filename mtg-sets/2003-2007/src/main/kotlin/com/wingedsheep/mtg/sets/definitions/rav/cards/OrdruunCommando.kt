package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ordruun Commando
 * {3}{R}
 * Creature — Minotaur Soldier
 * 4/1
 * {W}: Prevent the next 1 damage that would be dealt to this creature this turn.
 *
 * The shield names its own source, so the ability takes no target — [EffectTarget.Self] rather
 * than a `target(...)` handle.
 */
val OrdruunCommando = card("Ordruun Commando") {
    manaCost = "{3}{R}"
    colorIdentity = "WR"
    typeLine = "Creature — Minotaur Soldier"
    oracleText = "{W}: Prevent the next 1 damage that would be dealt to this creature this turn."
    power = 4
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.PreventNextDamage(1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Stephen Tappin"
        flavorText = "Thick of muscle, stout of heart, and possessing a burning love of justice and the battlefield, the Ordruun minotaurs are the foundation of the Boros Legion."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa726c0e-3a7e-4299-8842-4ce1f9f26567.jpg"
    }
}
