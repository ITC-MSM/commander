package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Soul of the Harvest
 * {4}{G}{G}
 * Creature — Elemental
 * 6/6
 *
 * Trample
 * Whenever another nontoken creature you control enters, you may draw a card.
 */
val SoulOfTheHarvest = card("Soul of the Harvest") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    oracleText = "Trample\nWhenever another nontoken creature you control enters, you may draw a card."
    power = 6
    toughness = 6

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().nontoken(),
            binding = TriggerBinding.OTHER,
        )
        optional = true
        effect = DrawCardsEffect(1)
        description = "Whenever another nontoken creature you control enters, you may draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "195"
        artist = "Eytan Zana"
        flavorText = "It's there when a seed sprouts, when gourds ripen on the vines, and when the reapers cut the grains under the Harvest Moon."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/078f5e79-18dd-44e5-a930-8dc288f0b535.jpg?1783940661"
    }
}
