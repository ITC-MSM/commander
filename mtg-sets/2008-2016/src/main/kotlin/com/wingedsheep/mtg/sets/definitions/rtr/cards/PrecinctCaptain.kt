package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Precinct Captain
 * {W}{W}
 * Creature — Human Soldier
 * 2/2
 *
 * First strike
 * Whenever this creature deals combat damage to a player, create a 1/1 white Soldier creature token.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * First strike and a combat-damage trigger interact through the engine's two damage steps: the
 * Captain's damage lands in the first-strike step, so the token is on the battlefield before
 * regular combat damage is dealt.
 */
val PrecinctCaptain = card("Precinct Captain") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "First strike\n" +
        "Whenever this creature deals combat damage to a player, create a 1/1 white Soldier creature token."
    power = 2
    toughness = 2

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Steve Prescott"
        flavorText = "\"In troubled times, we all need someone to watch our back.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f1f6178-4071-401f-bd0d-cac0c5967661.jpg?1783940375"
    }
}
