package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sunshot Militia
 * {1}{R}
 * Creature — Human Soldier
 * 1/3
 * Tap two untapped artifacts and/or creatures you control: This creature deals 1 damage to each
 * opponent. Activate only as a sorcery.
 *
 * The activation cost reuses the [Costs.TapPermanents] primitive (same shape as Adaptive Gemguard /
 * Goldfury Strider): tap exactly two untapped artifacts or creatures you control (no "other"
 * restriction — Sunshot Militia itself may be one of the two). The effect deals 1 damage to each
 * opponent via [Effects.DealDamage] against [Player.EachOpponent].
 */
val SunshotMilitia = card("Sunshot Militia") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 3
    oracleText = "Tap two untapped artifacts and/or creatures you control: This creature deals 1 damage to each opponent. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Artifact or GameObjectFilter.Creature,
        )
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Torgeir Fjereide"
        flavorText = "As the Legion of Dusk closed in, Oltec citizens rallied to support the Thousand Moons and drive off the invaders."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3114f5c-21e9-43c3-abe3-3cf1da20916f.jpg?1782694475"
    }
}
