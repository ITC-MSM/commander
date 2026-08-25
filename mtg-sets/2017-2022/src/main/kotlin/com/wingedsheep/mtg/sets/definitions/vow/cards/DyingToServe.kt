package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dying to Serve — Innistrad: Crimson Vow #109
 * {2}{B} · Enchantment
 *
 * Whenever you discard one or more cards, create a tapped 2/2 black Zombie creature token.
 * This ability triggers only once each turn.
 *
 * Two riders, two different mechanisms, and they are not interchangeable:
 *
 * - **"one or more cards"** is CR 603.2c batch wording, so the trigger is
 *   [Triggers.YouDiscardOneOrMore] rather than the per-card `YouDiscard`. A discard of three cards
 *   is one event and makes one Zombie, not three. Sequential discards inside one resolution
 *   ("discard a card, then discard a card") are separate events and would fire separately — which
 *   is exactly what the second rider is there to stop.
 * - **"only once each turn"** caps the *ability*, not the event, so it is `oncePerTurn = true` on
 *   the ability rather than anything on the trigger pattern.
 *
 * The token enters tapped (`tapped = true`), which is a property of the token being created and not
 * a separate tap effect — no "when this enters" window opens between the two.
 */
val DyingToServe = card("Dying to Serve") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Whenever you discard one or more cards, create a tapped 2/2 black Zombie " +
        "creature token. This ability triggers only once each turn."

    triggeredAbility {
        trigger = Triggers.YouDiscardOneOrMore
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            tapped = true,
            imageUri = "https://cards.scryfall.io/normal/front/c/8/c84e21cd-079d-493f-ab8d-e62f16ec1581.jpg?1782739822",
        )
        oncePerTurn = true
        description = "Whenever you discard one or more cards, create a tapped 2/2 black Zombie " +
            "creature token. This ability triggers only once each turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "109"
        artist = "Steven Belledin"
        flavorText = "\"I used to have hired hands, but I discovered they make better waitstaff " +
            "with the rest of the body attached.\"\n—Olivia Voldaren"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2ea16cd-801e-478a-b924-5431582b70d1.jpg?1783924866"
    }
}
