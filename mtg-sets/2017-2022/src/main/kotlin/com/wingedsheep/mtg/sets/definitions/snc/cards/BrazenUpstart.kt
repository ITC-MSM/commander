package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Brazen Upstart
 * {R}{G}{W}
 * Creature — Elf Shaman
 * 4 / 2
 * Vigilance
 * When this creature dies, look at the top five cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
 */
val BrazenUpstart = card("Brazen Upstart") {
    manaCost = "{R}{G}{W}"
    colorIdentity = "GRW"
    typeLine = "Creature — Elf Shaman"
    oracleText = "Vigilance\nWhen this creature dies, look at the top five cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order."
    power = 4
    toughness = 2

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(5),
            filter = GameObjectFilter.Creature,
            prompt = "You may reveal a creature card from among them and put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "169"
        artist = "Dallas Williams"
        flavorText = "\"Trust me, you want to deal with me. My associates are much less pleasant.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3ae69722-9cb9-4fb7-830f-a284d4a72027.jpg?1783923093"
    }
}
