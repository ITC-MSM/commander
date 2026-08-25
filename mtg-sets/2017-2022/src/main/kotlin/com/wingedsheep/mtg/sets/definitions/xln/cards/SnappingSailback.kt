package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Snapping Sailback
 * {4}{G}
 * Creature — Dinosaur
 * 4/4
 *
 * Flash
 * Enrage — Whenever this creature is dealt damage, put a +1/+1 counter on it.
 */
val SnappingSailback = card("Snapping Sailback") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Flash\n" +
        "Enrage — Whenever this creature is dealt damage, put a +1/+1 counter on it. " +
        "(It must survive the damage to get the counter.)"
    power = 4
    toughness = 4

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Enrage — Whenever this creature is dealt damage, put a +1/+1 counter on it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Dan Murayama Scott"
        flavorText = "Lurking beneath the murky waters of Ixalan's rivers, sailbacks can rip a meal off the shore in the blink of an eye."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0af1eadf-f7ea-40be-a0cc-b79e4161db34.jpg"
    }
}
