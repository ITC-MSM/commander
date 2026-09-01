package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Soul of Magma
 * {3}{R}{R}
 * Creature — Spirit
 * 2/2
 * Whenever you cast a Spirit or Arcane spell, this creature deals 1 damage to target creature.
 *
 * The shared CHK "Whenever you cast a Spirit or Arcane spell" trigger — [Triggers.youCastSpell]
 * over a homogeneous OR of the two subtype filters, binding `ANY` — with a mandatory ping as its
 * payoff.
 *
 * The damage source is left implicit rather than passed as an explicit `damageSource`: with no
 * override, [Effects.DealDamage] attributes the damage to the ability's own source, which is
 * exactly what "this creature deals 1 damage" means (and what makes the damage red and lifelink-
 * or deathtouch-aware should Soul of Magma ever gain either).
 */
val SoulOfMagma = card("Soul of Magma") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, this creature deals 1 damage to " +
        "target creature."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val creature = target("target", Targets.Creature)
        effect = Effects.DealDamage(1, creature)
        description = "Whenever you cast a Spirit or Arcane spell, this creature deals 1 damage " +
            "to target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Darrell Riche"
        flavorText = "\"In every other mind, the battle was lost. General Takeno alone was not " +
            "touched by despair. Drawing his blade, he was attack and rallying cry in one.\"\n" +
            "—*Battle of Akagi River: A Survivor's Tale*"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40b20126-da96-4d79-8d3f-8d7da8e94f4a.jpg?1783944295"
    }
}
