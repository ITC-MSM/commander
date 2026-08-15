package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brass Man
 * {1}
 * Artifact Creature — Construct
 * 1/3
 * This creature doesn't untap during your untap step.
 * At the beginning of your upkeep, you may pay {1}. If you do, untap this creature.
 */
val BrassMan = card("Brass Man") {
    manaCost = "{1}"
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 3
    oracleText = "This creature doesn't untap during your untap step.\nAt the beginning of your upkeep, you may pay {1}. If you do, untap this creature."

    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = MayPayManaEffect(ManaCost.parse("{1}"), Effects.Untap(EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a364362-e42b-415c-9d95-b6ec7139f5e7.jpg?1562899900"
    }
}
