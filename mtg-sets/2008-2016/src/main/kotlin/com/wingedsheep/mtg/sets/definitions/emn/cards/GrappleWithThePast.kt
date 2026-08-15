package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Grapple with the Past
 * {1}{G}
 * Instant — Common (EMN #160)
 *
 * "Mill three cards, then you may return a creature or land card from your graveyard to your hand."
 *
 * Implementation:
 *  - [Patterns.Library.mill] first, so the three milled cards are already in the graveyard and are
 *    legal picks for the return step (CR 608.2 — the spell's instructions resolve in printed order).
 *  - The "you may return a … card" clause is a `chooseUpTo(1)` over the gathered graveyard pool
 *    rather than a yes/no + forced pick: declining is just choosing zero, and an empty pool
 *    auto-skips the prompt. Modelling it as "up to one" also keeps the player from being asked to
 *    confirm before seeing what the mill turned up.
 *  - The card is not a target ("return a creature or land card", no "target"), so the selection is
 *    a resolution-time choice, not a `TargetRequirement`.
 */
val GrappleWithThePast = card("Grapple with the Past") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Mill three cards, then you may return a creature or land card from your graveyard " +
        "to your hand. (To mill three cards, put the top three cards of your library into your graveyard.)"

    spell {
        effect = Effects.Pipeline {
            // "Mill three cards, ..."
            run(Patterns.Library.mill(3))
            // "... then you may return a creature or land card from your graveyard to your hand."
            val graveyard = gather(
                CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.CreatureOrLand)
            )
            val chosen = chooseUpTo(
                1,
                from = graveyard,
                showAllCards = true,
                prompt = "You may return a creature or land card from your graveyard to your hand",
                selectedLabel = "Return to hand",
                remainderLabel = "Leave in graveyard"
            )
            toHand(chosen)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Howard Lyon"
        flavorText = "Emrakul does not grant wishes. Desires simply align to her will."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d44a77a6-e8a1-4706-886f-8ab3af56b342.jpg?1783937443"
    }
}
