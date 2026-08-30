package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Timberland Guide — Avacyn Restored #197
 * {1}{G} · Creature — Human Scout · 1/1
 *
 * When this creature enters, put a +1/+1 counter on target creature.
 *
 * The trigger's target may legally be the Guide itself, which is already on the battlefield when
 * the ability is put on the stack.
 */
val TimberlandGuide = card("Timberland Guide") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Scout"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, put a +1/+1 counter on target creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Zoltan Boros"
        flavorText = "\"Can you build a fire? Track a deer? Identify killer trees? Then you'll never survive Kessig without me.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae80fefb-af78-4f98-8058-71b61e91842f.jpg?1783940665"
    }
}
