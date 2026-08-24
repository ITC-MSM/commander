package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sumala Woodshaper
 * {2}{G}{W}
 * Creature — Elf Druid
 * 2/1
 * When this creature enters, look at the top four cards of your library. You may reveal a creature or enchantment card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
 */
val SumalaWoodshaper = card("Sumala Woodshaper") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Druid"
    oracleText = "When this creature enters, look at the top four cards of your library. You may reveal a creature or enchantment card from among them and put it into your hand. Put the rest on the bottom of your library in a random order."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.CreatureOrEnchantment,
            prompt = "You may reveal a creature or enchantment card from among them and " +
                "put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Sara Winters"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c4e427e-c061-43c5-9e8e-34bf5b447ab1.jpg?1783934124"
    }
}
