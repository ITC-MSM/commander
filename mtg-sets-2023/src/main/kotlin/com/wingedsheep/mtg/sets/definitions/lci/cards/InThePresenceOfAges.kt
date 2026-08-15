package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * In the Presence of Ages
 * {2}{G}
 * Instant — common #192 (LCI, Steve Prescott)
 *
 * Reveal the top four cards of your library. You may put a creature card and/or a land card from
 * among them into your hand. Put the rest into your graveyard.
 *
 * Pipeline (MemoriesReturning storeRemainder-chain pattern):
 *   1. GatherCards(TopOfLibrary(4)) → "top4"
 *   2. RevealCollection("top4")        — publicly face-up for all players
 *   3. SelectFromCollection("top4", ChooseUpTo(1), filter = Creature)
 *        → storeSelected = "chosenCreature", storeRemainder = "afterCreature"
 *   4. SelectFromCollection("afterCreature", ChooseUpTo(1), filter = Land)
 *        → storeSelected = "chosenLand", storeRemainder = "rest"
 *   5. MoveCollection("chosenCreature" → HAND, revealed = true)
 *   6. MoveCollection("chosenLand"     → HAND, revealed = true)
 *   7. MoveCollection("rest"           → GRAVEYARD)
 *
 * Chaining storeRemainder from the creature pick into the land pick's `from` ensures the second
 * selection never re-offers a card already taken by the first, honoring the "and/or" constraint
 * (each physical card can be chosen at most once).
 */
val InThePresenceOfAges = card("In the Presence of Ages") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Reveal the top four cards of your library. You may put a creature card and/or a " +
        "land card from among them into your hand. Put the rest into your graveyard."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(4)),
                    storeAs = "top4"
                ),
                RevealCollectionEffect(from = "top4"),
                // Step 1 of 2: optionally take one creature card.
                SelectFromCollectionEffect(
                    from = "top4",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Creature,
                    storeSelected = "chosenCreature",
                    storeRemainder = "afterCreature",
                    prompt = "You may put a creature card into your hand",
                    showAllCards = true
                ),
                // Step 2 of 2: optionally take one land card from whatever is left.
                SelectFromCollectionEffect(
                    from = "afterCreature",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Land,
                    storeSelected = "chosenLand",
                    storeRemainder = "rest",
                    prompt = "You may put a land card into your hand",
                    showAllCards = true
                ),
                MoveCollectionEffect(
                    from = "chosenCreature",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                ),
                MoveCollectionEffect(
                    from = "chosenLand",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                ),
                MoveCollectionEffect(
                    from = "rest",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Steve Prescott"
        flavorText = "\"Finally, those glowing purple bone-monsters are gone and we can get back to the real excitement: archaeology!\"\n—Quintorius Kand"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97b1be22-3177-49af-bb06-42497d717c21.jpg?1782694455"
    }
}
