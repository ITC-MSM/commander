package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

val PiaAetherAscetic = card("Pia, Aether Ascetic") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Druid"
    power = 2
    toughness = 2
    oracleText = "When Pia enters, you may discard a card. If you do, search your library for an enchantment " +
        "card, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Enchantment,
                    count = 1,
                    destination = SearchDestination.HAND,
                    reveal = true,
                    shuffleAfter = true
                )
            ),
            descriptionOverride = "You may discard a card. If you do, search your library for an enchantment " +
                "card, reveal it, put it into your hand, then shuffle."
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "264"
        artist = "Andreas Zafiratos"
        flavorText = "\"Perhaps one day I will learn how to break the hold the Consulate has over Chandra and Kiran.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff0bc30f-9d20-458e-808f-bdc2825905a5.jpg?1789387120"
        inBooster = false
    }
}
