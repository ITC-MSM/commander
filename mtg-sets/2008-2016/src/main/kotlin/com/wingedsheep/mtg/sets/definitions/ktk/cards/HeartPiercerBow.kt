package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Heart-Piercer Bow
 * {2}
 * Artifact — Equipment
 * Whenever equipped creature attacks, Heart-Piercer Bow deals 1 damage to
 * target creature defending player controls.
 * Equip {1}
 *
 * Note: The damage source is the Equipment itself, not the equipped creature.
 * "Defending player controls" is equivalent to "opponent controls" in the
 * context of a triggered ability during your attack.
 */
val HeartPiercerBow = card("Heart-Piercer Bow") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Whenever equipped creature attacks, Heart-Piercer Bow deals 1 damage to target creature defending player controls.\nEquip {1}"

    triggeredAbility {
        trigger = Triggers.attacks(binding = TriggerBinding.ATTACHED)
        val creature = target("creature defending player controls", Targets.CreatureOpponentControls)
        effect = DealDamageEffect(
            amount = DynamicAmount.Fixed(1),
            target = creature,
            damageSource = EffectTarget.Self
        )
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Franz Vohwinkel"
        flavorText = "\"Designed by an ancient artificer, the finest Mardu bows are carved from dragon bone and strung with the wind itself.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48168005-65cc-43dc-9d45-17ea5dd4848f.jpg?1562786011"
    }
}
