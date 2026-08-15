package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Metathran Zombie
 * {1}{U}
 * Creature — Metathran Zombie
 * 1/1
 * {B}: Regenerate this creature.
 */
val MetathranZombie = card("Metathran Zombie") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Metathran Zombie"
    power = 1
    toughness = 1
    oracleText = "{B}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/6676a0f7-8213-4547-b2ac-b904cd418073.jpg?1562915761"
    }
}
