package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bloodbond Vampire
 * {2}{B}{B}
 * Creature — Vampire Shaman Ally
 * 3/3
 * Whenever you gain life, put a +1/+1 counter on this creature.
 */
val BloodbondVampire = card("Bloodbond Vampire") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shaman Ally"
    power = 3
    toughness = 3
    oracleText = "Whenever you gain life, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Anna Steinbauer"
        flavorText = "\"Let the vampires join us. In this war, we no longer have the luxury of choosing our " +
            "comrades.\"\n" +
            "—General Tazri, allied commander"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca31ce70-24c5-4fb2-8d88-c9ffa8474c8f.jpg?1783938203"
    }
}
