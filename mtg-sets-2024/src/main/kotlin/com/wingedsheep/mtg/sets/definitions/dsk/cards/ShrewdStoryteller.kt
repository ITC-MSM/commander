package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shrewd Storyteller
 * {1}{G}{W}
 * Creature — Human Survivor
 * 3/3
 * Survival — At the beginning of your second main phase, if this creature is tapped, put a
 * +1/+1 counter on target creature.
 */
val ShrewdStoryteller = card("Shrewd Storyteller") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Human Survivor"
    power = 3
    toughness = 3
    oracleText = "Survival — At the beginning of your second main phase, if this creature is " +
        "tapped, put a +1/+1 counter on target creature."

    // Survival — At the beginning of your second main phase, if this creature is tapped, put a
    // +1/+1 counter on target creature.
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "David Palumbo"
        flavorText = "All his tales are cautionary."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9636877-8fcc-4ad5-8eb2-a5d5ba49583d.jpg?1726286734"
    }
}
