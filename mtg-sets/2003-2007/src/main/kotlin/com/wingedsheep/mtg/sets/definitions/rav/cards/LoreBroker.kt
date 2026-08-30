package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Lore Broker
 * {1}{U}
 * Creature — Human Rogue
 * 1/2
 *
 * {T}: Each player draws a card, then discards a card.
 *
 * Every player draws before any player discards, so the freshly drawn card is a legal
 * discard. Both halves iterate in APNAP order (CR 101.4).
 */
val LoreBroker = card("Lore Broker") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText = "{T}: Each player draws a card, then discards a card."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        effect = ForEachPlayerEffect(Player.ActivePlayerFirst, listOf(Effects.DrawCards(1))) then
            Patterns.Hand.eachPlayerDiscards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Alan Pollack"
        flavorText = "Lies are sold as often as truths—and used just as effectively."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7935b2b-aebe-44b3-b91b-52978bb4ded5.jpg?1783943683"
    }
}
