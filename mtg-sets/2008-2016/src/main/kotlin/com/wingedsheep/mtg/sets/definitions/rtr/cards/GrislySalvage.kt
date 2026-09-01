package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Grisly Salvage
 * {B}{G}
 * Instant
 *
 * Reveal the top five cards of your library. You may put a creature or land card from among them into your hand. Put the rest into your graveyard.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * [Patterns.Library.lookAtTopAndTakeMatching] is the gather → filtered up-to-one select → move
 * pipeline this sentence names. The defaults it overrides are the two the card prints: the
 * cards are *revealed* rather than looked at privately, and the remainder goes to the graveyard
 * in order rather than to the bottom of the library at random.
 */
val GrislySalvage = card("Grisly Salvage") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "Reveal the top five cards of your library. You may put a creature or land card from among them into your hand. Put the rest into your graveyard."

    spell {
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.Fixed(5),
            filter = GameObjectFilter.CreatureOrLand,
            prompt = "You may put a creature or land card from among them into your hand",
            revealed = true,
            restDestination = CardDestination.ToZone(Zone.GRAVEYARD),
            restOrder = CardOrder.Preserve,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Dave Kendall"
        flavorText = "To the Golgari, anything buried is treasure."
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcb5eb2a-ae7a-4416-970c-6e9306689c88.jpg?1783940339"
    }
}
