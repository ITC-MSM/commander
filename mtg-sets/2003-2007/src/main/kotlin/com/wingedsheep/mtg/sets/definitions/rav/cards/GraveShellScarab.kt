package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.dsl.Effects


/**
 * Grave-Shell Scarab
 * {2}{B}{G}{G}
 * Creature — Insect
 * 4/4
 * {1}, Sacrifice this creature: Draw a card.
 * Dredge 1 (If you would draw a card, you may mill a card instead. If you do, return this card from your graveyard to your hand.)
 */
val GraveShellScarab = card("Grave-Shell Scarab") {
    manaCost = "{2}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Insect"
    oracleText = "{1}, Sacrifice this creature: Draw a card.\nDredge 1 (If you would draw a card, you may mill a card instead. If you do, return this card from your graveyard to your hand.)"
    power = 4
    toughness = 4
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }
    keywordAbility(KeywordAbility.dredge(1))
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "211"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23f7c7d0-a48b-4153-8386-a9ab71e01dbe.jpg?1783943619"
        ruling("2005-10-01", "You may sacrifice Grave-Shell Scarab to pay for its first ability, then replace that draw using the Scarab's dredge ability. The result is that the top card of your library is put into your graveyard and Grave-Shell Scarab returns to your hand (after a brief trip to the graveyard).")
    }
}
