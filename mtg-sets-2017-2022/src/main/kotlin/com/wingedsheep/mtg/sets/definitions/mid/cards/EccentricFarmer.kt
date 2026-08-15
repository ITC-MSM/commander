package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Eccentric Farmer
 * {2}{G}
 * Creature — Human Peasant — Common (MID #185)
 * 2/3
 *
 * "When this creature enters, mill three cards, then you may return a land card from your
 * graveyard to your hand."
 *
 * Implementation: the ETB half of the same shape as Grapple with the Past — mill first so the
 * milled cards are legal picks, then a `chooseUpTo(1)` over the graveyard's land cards. "You may
 * return a … card" is modelled as "up to one" (declining = choosing zero); an empty pool
 * auto-skips the prompt. No "target" in the oracle text, so this is a resolution-time choice.
 */
val EccentricFarmer = card("Eccentric Farmer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Peasant"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, mill three cards, then you may return a land card from " +
        "your graveyard to your hand. (To mill three cards, put the top three cards of your library " +
        "into your graveyard.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Pipeline {
            run(Patterns.Library.mill(3))
            val lands = gather(CardSource.FromZone(Zone.GRAVEYARD, Player.You, Filters.Land))
            val chosen = chooseUpTo(
                1,
                from = lands,
                showAllCards = true,
                prompt = "You may return a land card from your graveyard to your hand",
                selectedLabel = "Return to hand",
                remainderLabel = "Leave in graveyard"
            )
            toHand(chosen)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Tyler Walpole"
        flavorText = "\"Careful now. Don't want to get the seeds stuck to your feelers like last time.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5339f6e-9ed7-4734-91c9-c2ed59ca1052.jpg?1783925579"
    }
}
