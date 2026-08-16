package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blessed Spirits
 * {2}{W}
 * Creature — Spirit
 * 2/2
 * Flying
 * Whenever you cast an enchantment spell, put a +1/+1 counter on this creature.
 */
val BlessedSpirits = card("Blessed Spirits") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 2
    oracleText = "Flying\nWhenever you cast an enchantment spell, put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Anna Steinbauer"
        flavorText = "Not all heroes die in armor."
        imageUri = "https://cards.scryfall.io/normal/front/1/5/150dd602-5f61-4f1e-a422-e64c079de141.jpg?1783938364"

        ruling("2015-06-22", "Blessed Spirits's ability will resolve before the enchantment spell that caused it to trigger.")
    }
}
