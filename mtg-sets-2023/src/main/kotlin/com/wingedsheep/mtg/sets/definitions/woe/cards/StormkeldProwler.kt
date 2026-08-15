package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stormkeld Prowler
 * {1}{U}
 * Creature — Human Rogue
 * 2/1
 *
 * Whenever you cast a spell with mana value 5 or greater, put two +1/+1 counters on this creature.
 */
val StormkeldProwler = card("Stormkeld Prowler") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast a spell with mana value 5 or greater, put two +1/+1 counters on this creature."

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Any.manaValueAtLeast(5))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Zara Alfonso"
        flavorText = "To the giants, it was one little gem among thousands. But to Kylene, it was a haul that would feed her family for years."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff065dbf-77e3-45a8-bcca-aff9eaeb151f.jpg?1783915114"
    }
}
