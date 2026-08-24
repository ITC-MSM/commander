package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rampaging Monument
 * {4}
 * Artifact Creature — Cleric
 * 0/0
 * Trample
 * This creature enters with three +1/+1 counters on it.
 * Whenever you cast a multicolored spell, put a +1/+1 counter on this creature.
 */
val RampagingMonument = card("Rampaging Monument") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Cleric"
    oracleText = "Trample\n" +
        "This creature enters with three +1/+1 counters on it.\n" +
        "Whenever you cast a multicolored spell, put a +1/+1 counter on this creature."
    power = 0
    toughness = 0

    keywords(Keyword.TRAMPLE)
    replacementEffect(EntersWithCounters(count = 3, selfOnly = true))
    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Multicolored)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Tyler Walpole"
        flavorText = "\"Be advised: suspect is nine stories tall, marble hair, answers to Saint Gusztav.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5fbe445-a788-4624-8ecf-8bc06c3ca8f8.jpg?1783934106"
    }
}
