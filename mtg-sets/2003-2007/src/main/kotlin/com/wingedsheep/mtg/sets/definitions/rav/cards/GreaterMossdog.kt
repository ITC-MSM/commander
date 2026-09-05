package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility


/**
 * Greater Mossdog
 * {3}{G}
 * Creature — Plant Dog
 * 3/3
 * Dredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)
 */
val GreaterMossdog = card("Greater Mossdog") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Dog"
    oracleText = "Dredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)"
    power = 3
    toughness = 3
    keywordAbility(KeywordAbility.dredge(3))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Chippy"
        flavorText = "Man's best fungus."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1f75b44-01a3-47f6-96c9-9ce327111e64.jpg?1783943635"
    }
}
