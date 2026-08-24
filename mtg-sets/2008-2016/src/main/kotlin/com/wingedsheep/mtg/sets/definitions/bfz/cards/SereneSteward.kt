package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Serene Steward
 * {1}{W}
 * Creature — Human Cleric Ally
 * 2/2
 * Whenever you gain life, you may pay {W}. If you do, put a +1/+1 counter on target creature.
 */
val SereneSteward = card("Serene Steward") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric Ally"
    power = 2
    toughness = 2
    oracleText = "Whenever you gain life, you may pay {W}. If you do, put a +1/+1 counter on target creature."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        val creature = target("target creature", Targets.Creature)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{W}"),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Magali Villeneuve"
        flavorText = "\"I can give you strength, but you'll have to bring your own courage.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/702f5dd4-4dc7-4e2c-a30b-9286499433d8.jpg?1783938216"
    }
}
