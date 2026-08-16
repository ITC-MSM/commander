package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fierce Witchstalker
 * {2}{G}{G}
 * Creature — Wolf
 * 4/4
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 * When this creature enters, create a Food token. (It's an artifact with "{2}, {T}, Sacrifice this token: You gain 3 life.")
 */
val FierceWitchstalker = card("Fierce Witchstalker") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 4
    toughness = 4
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)\nWhen this creature enters, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Nicholas Gregory"
        flavorText = "While the realm has laws, in the wilds there are other ways of balancing power."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d63a6be2-ae9a-4758-9d5c-0297ef9af57c.jpg?1783932611"
        ruling("2024-11-08", "Food is an artifact type. Even though it appears on some creatures, it's never a creature type.")
        ruling("2024-11-08", "You can't sacrifice a Food to pay multiple costs. For example, you can't sacrifice a Food token to activate its own ability and also to activate Maraleaf Rider's ability.")
    }
}
