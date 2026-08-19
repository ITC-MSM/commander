package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Elvish Handservant
 * {G}
 * Creature — Elf Warrior
 * 1/1
 * Whenever a player casts a Giant spell, you may put a +1/+1 counter on this creature.
 */
val ElvishHandservant = card("Elvish Handservant") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 1
    toughness = 1
    oracleText = "Whenever a player casts a Giant spell, you may put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withSubtype(Subtype.GIANT))
        optional = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever a player casts a Giant spell, you may put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "206"
        artist = "Steve Prescott"
        flavorText = "The hardest lesson for any elf to learn is humility. It takes a giant to teach that."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/9854f9a9-160c-4c96-b871-0f85914597d0.jpg?1783942866"
    }
}
