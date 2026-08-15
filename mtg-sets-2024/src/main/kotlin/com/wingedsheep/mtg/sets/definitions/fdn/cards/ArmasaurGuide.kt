package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.YouAttackEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Armasaur Guide
 * {4}{W}
 * Creature — Dinosaur
 * 4/4
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * Whenever you attack with three or more creatures, put a +1/+1 counter on target creature you control.
 */
val ArmasaurGuide = card("Armasaur Guide") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    power = 4
    toughness = 4
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "Whenever you attack with three or more creatures, put a +1/+1 counter on target creature you control."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = TriggerSpec(YouAttackEvent(minAttackers = 3), TriggerBinding.ANY)
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Loïc Canavaggia"
        flavorText = "The armasaur was skittish about the lanterns, until it noticed the captivating dance of its shadow along the walls."
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c80fc380-0499-4499-8a60-c43844c02c9b.jpg?1782689262"
    }
}
