package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Master the Way
 * {3}{U}{R}
 * Sorcery
 * Draw a card. Master the Way deals damage to any target equal to the number of cards in your hand.
 *
 * Rulings:
 * - Use the number of cards in your hand after you draw the card to determine how much damage
 *   Master the Way deals to the target permanent or player.
 */
val MasterTheWay = card("Master the Way") {
    manaCost = "{3}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Sorcery"
    oracleText = "Draw a card. Master the Way deals damage to any target equal to the number of cards in your hand."

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.DrawCards(1)
            .then(Effects.DealDamage(DynamicAmount.Count(Player.You, Zone.HAND), t))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Howard Lyon"
        flavorText = "\"The Way has no beginning and no end. It is simply the path.\" —Narset, khan of the Jeskai"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/704743a8-27d6-4db9-8fe1-f65f5f3a955f.jpg?1562788383"
    }
}
