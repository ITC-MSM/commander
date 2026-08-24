package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Beast Whisperer
 * {2}{G}{G}
 * Creature — Elf Druid
 * 2/3
 * Whenever you cast a creature spell, draw a card.
 */
val BeastWhisperer = card("Beast Whisperer") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Whenever you cast a creature spell, draw a card."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Creature)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Matt Stewart"
        flavorText = "\"The tiniest mouse speaks louder to me than all the festival crowds on Tin Street.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9da6f595-41b2-4e52-b15a-6ad18e4232c7.jpg?1783934153"
    }
}
