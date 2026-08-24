package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Golgari Findbroker
 * {B}{B}{G}{G}
 * Creature — Elf Shaman
 * 3/4
 * When this creature enters, return target permanent card from your graveyard to your hand.
 */
val GolgariFindbroker = card("Golgari Findbroker") {
    manaCost = "{B}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Shaman"
    oracleText = "When this creature enters, return target permanent card from your graveyard to your hand."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target("target", TargetObject(filter = TargetFilter.PermanentInYourGraveyard))
        effect = Effects.ReturnToHand(card)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Bram Sels"
        flavorText = "\"We gather the past from surface dwellers and sell it right back to them.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e12bd0e5-db96-4340-9b04-6855fd0fd8b9.jpg?1783934133"
    }
}
