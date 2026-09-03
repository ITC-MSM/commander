package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kronch Wrangler
 * {1}{G}
 * Creature — Human Warrior
 * 2/1
 * Trample
 * Whenever a creature you control with power 4 or greater enters, put a +1/+1 counter on this creature.
 */
val KronchWrangler = card("Kronch Wrangler") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    oracleText = "Trample\n" +
        "Whenever a creature you control with power 4 or greater enters, put a +1/+1 counter on this creature."
    power = 2
    toughness = 1
    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.powerAtLeast(4).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Steve Prescott"
        flavorText = "Looking over two city blocks of destruction, he could only cackle with pride."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f41d22a-ee37-4d69-a9ca-98c91315b9e6.jpg"
    }
}
