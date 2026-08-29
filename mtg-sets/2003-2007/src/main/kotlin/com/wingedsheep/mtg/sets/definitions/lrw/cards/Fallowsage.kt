package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Fallowsage
 * {3}{U}
 * Creature — Merfolk Wizard
 * 2/2
 * Whenever this creature becomes tapped, you may draw a card.
 *
 * [Triggers.BecomesTapped] is the SELF binding, so it fires however Fallowsage became tapped —
 * attacking, an activation cost, or another player's effect.
 */
val Fallowsage = card("Fallowsage") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature becomes tapped, you may draw a card."

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = MayEffect(Effects.DrawCards(1))
        description = "Whenever this creature becomes tapped, you may draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Paolo Parente"
        flavorText = "Memories of ages past are said to swim the minds of lounging fallowsages."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4237352-4e0e-4f40-946b-2a61753674d4.jpg?1783942904"
    }
}
