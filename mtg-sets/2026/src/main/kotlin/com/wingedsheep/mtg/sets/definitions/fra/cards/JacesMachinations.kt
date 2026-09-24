package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Jace's Machinations
 *
 * The first sentence is a turn-scoped player permission lifting only the sorcery-timing half of
 * CR 606.3 for Jace planeswalkers you control — each planeswalker's once-per-turn limit still
 * applies. It is recorded before the empower, so the Jace token that empower may create is covered
 * too. The filter is read when an ability is offered, so a Jace that enters later this turn is also
 * covered.
 */
val JacesMachinations = card("Jace's Machinations") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, you may activate loyalty abilities of Jace planeswalkers you control on any player's turn any time you could cast an instant.\n" +
        "Empower Jace 8. (Put eight loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        effect = Effects.Composite(
            Effects.InstantSpeedLoyaltyAbilities(
                planeswalkerFilter = GameObjectFilter.Planeswalker.withSubtype("Jace").youControl(),
            ),
            Patterns.Mechanic.empowerJace(8),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "32"
        artist = "Serena Malyon"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/282588b9-3656-453b-aa25-2419e078ddc1.jpg?1788878150"
        inBooster = false
    }
}
