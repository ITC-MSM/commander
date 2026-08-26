package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Mesa Enchantress
 * {1}{W}{W}
 * Creature — Human Druid
 * 0/2
 * Whenever you cast an enchantment spell, you may draw a card.
 */
val MesaEnchantress = card("Mesa Enchantress") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Druid"
    power = 0
    toughness = 2
    oracleText = "Whenever you cast an enchantment spell, you may draw a card."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = MayEffect(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "26"
        artist = "Randy Gallegos"
        flavorText = "She shepherds mysteries and dust as others would a flock of sheep."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/4037d6de-f30b-483c-83a8-9a4e2978f7fc.jpg"
    }
}
