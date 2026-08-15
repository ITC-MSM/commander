package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sporogenic Infection
 * {1}{B}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, target player sacrifices a creature of their choice other than enchanted
 * creature.
 * When enchanted creature is dealt damage, destroy it.
 *
 * Standard Aura wiring: [auraTarget] enchants a creature (Targets.Creature), then two triggered
 * abilities. The enters trigger is an edict — it names its own "target player" and forces that
 * player to sacrifice a creature *of their choice* (the default [Effects.Sacrifice] / ForceSacrifice
 * behaviour) restricted to a creature other than the one this Aura is attached to via the
 * source-relative [GameObjectFilter.notAttachedToBySource] exclusion. The damage trigger uses the
 * shared [Triggers.takesDamage] factory with [TriggerBinding.ATTACHED] ("enchanted creature is dealt
 * damage") and destroys it via [EffectTarget.EnchantedCreature].
 */
val SporogenicInfection = card("Sporogenic Infection") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, target player sacrifices a creature of their choice other than " +
        "enchanted creature.\n" +
        "When enchanted creature is dealt damage, destroy it."

    auraTarget = Targets.Creature

    // When this Aura enters, target player sacrifices a creature of their choice other than
    // enchanted creature.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val player = target("target player", Targets.Player)
        effect = Effects.Sacrifice(
            filter = GameObjectFilter.Creature.notAttachedToBySource(),
            count = 1,
            target = player
        )
        description = "Target player sacrifices a creature of their choice other than enchanted creature."
    }

    // When enchanted creature is dealt damage, destroy it.
    triggeredAbility {
        trigger = Triggers.takesDamage(binding = TriggerBinding.ATTACHED)
        effect = Effects.Destroy(EffectTarget.EnchantedCreature)
        description = "Destroy enchanted creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaae086e-0781-4f4d-bc9c-a98228bc380c.jpg?1726286285"
    }
}
