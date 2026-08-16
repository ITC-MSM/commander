package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Windrider Patrol
 * {3}{U}{U}
 * Creature — Merfolk Wizard
 * 4/3
 * Flying
 * Whenever this creature deals combat damage to a player, scry 2.
 *
 * Printed flying plus the Jeskai Elder trigger shape — [Triggers.DealsCombatDamageToPlayer] feeding
 * [Effects.Scry]`(2)`.
 */
val WindriderPatrol = card("Windrider Patrol") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 4
    toughness = 3
    oracleText = "Flying\nWhenever this creature deals combat damage to a player, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.Scry(2)
        description = "Scry 2."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ac7b904-7658-4248-a75c-b3a862bde196.jpg?1783938206"
    }
}
