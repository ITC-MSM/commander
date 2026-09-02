package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stealer of Secrets
 * {2}{U}
 * Creature — Human Rogue
 * 2/2
 *
 * Whenever this creature deals combat damage to a player, draw a card.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The plain combat-damage-to-a-player trigger over a draw.
 */
val StealerOfSecrets = card("Stealer of Secrets") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText = "Whenever this creature deals combat damage to a player, draw a card."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Michael C. Hayes"
        flavorText = "The Dimir would hire her, if only they knew where she lived. The Azorius would condemn her, if only they knew her name."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30ae7001-4d0f-4160-b41c-2fcb83fdb60b.jpg?1783940366"
    }
}
