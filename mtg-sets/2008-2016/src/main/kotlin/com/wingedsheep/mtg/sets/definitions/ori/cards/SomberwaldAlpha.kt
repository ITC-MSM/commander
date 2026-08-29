package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Somberwald Alpha
 * {3}{G}
 * Creature — Wolf
 * 3/2
 *
 * Whenever a creature you control becomes blocked, it gets +1/+1 until end of turn.
 * {1}{G}: Target creature you control gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 *
 * "it" in the trigger is the blocked creature, not the Alpha, so the pump targets
 * [EffectTarget.TriggeringEntity] — the ANY-bound `becomesBlocked` binds the creature that became
 * blocked, which is exactly what the pronoun refers to.
 */
val SomberwaldAlpha = card("Somberwald Alpha") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    oracleText = "Whenever a creature you control becomes blocked, it gets +1/+1 until end of turn.\n" +
        "{1}{G}: Target creature you control gains trample until end of turn. (It can deal excess " +
        "combat damage to the player or planeswalker it's attacking.)"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.becomesBlocked(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.TriggeringEntity)
    }

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        val t = target("target creature you control", TargetCreature(filter = TargetFilter.CreatureYouControl))
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/492808ec-7e23-4266-adc5-519e74a06bbb.jpg"
    }
}
