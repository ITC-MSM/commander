package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility

/**
 * Illvoi Infiltrator
 * {2}{U}
 * Creature — Jellyfish Rogue
 * This creature can't be blocked if you've cast two or more spells this turn.
 * Whenever this creature deals combat damage to a player, draw a card.
 * 1/3
 */
val IllvoiInfiltrator = card("Illvoi Infiltrator") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Jellyfish Rogue"
    power = 1
    toughness = 3
    oracleText = "This creature can't be blocked if you've cast two or more spells this turn.\nWhenever this creature deals combat damage to a player, draw a card."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBeBlocked(),
            condition = Conditions.YouCastSpellsThisTurn(atLeast = 2)
        )
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Paolo Parente"
        flavorText = "Cloudkill agents slip in and out like a breeze."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2db4ed41-0426-4d7f-bb43-e43392bed83b.jpg?1752946786"
    }
}
