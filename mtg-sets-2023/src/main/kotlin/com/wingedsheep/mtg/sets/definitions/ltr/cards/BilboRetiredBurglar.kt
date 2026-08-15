package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bilbo, Retired Burglar
 * {1}{U}{R}
 * Legendary Creature — Halfling Rogue
 * 1/3
 *
 * When Bilbo enters or leaves the battlefield, the Ring tempts you.
 * Whenever Bilbo deals combat damage to a player, create a Treasure token.
 */
val BilboRetiredBurglar = card("Bilbo, Retired Burglar") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Halfling Rogue"
    power = 1
    toughness = 3
    oracleText = "When Bilbo enters or leaves the battlefield, the Ring tempts you.\n" +
        "Whenever Bilbo deals combat damage to a player, create a Treasure token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "196"
        artist = "Anna Pavleeva"
        flavorText = "\"I don't know half of you half as well as I should like; and I like less than half of you half as well as you deserve.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/2/527df93e-cc2b-4216-909a-4ada1abcbfd3.jpg?1687694657"
    }
}
