package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cunning Evasion
 * {1}{U}
 * Enchantment
 * Whenever a creature you control becomes blocked, you may return it to its owner's hand.
 *
 * The trigger watches every creature its controller controls, so it is
 * [Triggers.becomesBlocked] with an ANY binding — the enchantment itself is never the blocked
 * creature. "It" is the creature that became blocked, i.e. [EffectTarget.TriggeringEntity].
 */
val CunningEvasion = card("Cunning Evasion") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control becomes blocked, you may return it to its owner's hand."

    triggeredAbility {
        trigger = Triggers.becomesBlocked(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = MayEffect(Effects.ReturnToHand(EffectTarget.TriggeringEntity))
        description = "Whenever a creature you control becomes blocked, you may return it to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Steve Argyle"
        flavorText = "\"Begin with your escape and work backward.\"\n—*Way of Secrets*"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/300a431c-48a9-4d63-8256-cb6bdaa67b4c.jpg?1783933148"
    }
}
