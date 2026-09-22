package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ArniHumbleScribe = card("Arni, Humble Scribe") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Wizard"
    oracleText = "Whenever another nontoken creature you control enters, untap Arni.\n{T}: Draw a card, then discard a card."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().nontoken(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Vincent Christiaens"
        flavorText = "\"If you have a tale worthy of telling, I would hear it, friend.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e3a2239-9348-4639-9318-e9e35b2cf86b.jpg?1789568409"
        inBooster = false
    }
}
