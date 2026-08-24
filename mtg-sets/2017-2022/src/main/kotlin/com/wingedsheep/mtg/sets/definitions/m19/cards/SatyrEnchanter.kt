package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Satyr Enchanter
 * {1}{G}{W}
 * Creature — Satyr Druid
 * 2/2
 * Whenever you cast an enchantment spell, draw a card.
 */
val SatyrEnchanter = card("Satyr Enchanter") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Satyr Druid"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast an enchantment spell, draw a card."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Sidharth Chaturvedi"
        flavorText = "\"The threads of magic that protect this place were woven by my will.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e31c544f-a748-4180-8366-9bb1622bb99d.jpg"
    }
}
