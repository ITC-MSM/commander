package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Famished Paladin
 * {1}{W}
 * Creature — Vampire Knight
 * 3/3
 * This creature doesn't untap during your untap step.
 * Whenever you gain life, untap this creature.
 *
 * [Triggers.YouGainLife] already carries `TriggerBinding.ANY`: the printed line watches every
 * life gain you get, not only the ones this creature causes.
 */
val FamishedPaladin = card("Famished Paladin") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Knight"
    oracleText = "This creature doesn't untap during your untap step.\n" +
        "Whenever you gain life, untap this creature."
    power = 3
    toughness = 3

    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Tommy Arnold"
        flavorText = "Loyal to his queen, slave to his thirst."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d80e3bc9-7b67-4eab-916a-6d83da06f20a.jpg?1783935339"
    }
}
