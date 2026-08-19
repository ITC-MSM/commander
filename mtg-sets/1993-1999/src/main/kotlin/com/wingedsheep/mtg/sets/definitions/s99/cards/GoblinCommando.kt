package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Goblin Commando
 * {4}{R}
 * Creature — Goblin
 * 2/2
 *
 * When this creature enters, it deals 2 damage to target creature.
 */
val GoblinCommando = card("Goblin Commando") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    oracleText = "When this creature enters, it deals 2 damage to target creature."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(2, creature)
        description = "When this creature enters, it deals 2 damage to target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Todd Lockwood"
        flavorText = "With a commando around, somebody's gonna get hurt."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da90043b-0e67-4a68-b6fb-0ca53ca7defc.jpg?1783946030"
    }
}
