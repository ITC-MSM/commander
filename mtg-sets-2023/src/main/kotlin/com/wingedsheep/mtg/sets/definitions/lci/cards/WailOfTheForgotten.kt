package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wail of the Forgotten
 * {U}{B}
 * Sorcery
 *
 * Descend 8 — Choose one. If there are eight or more permanent cards in your graveyard as you
 * cast this spell, choose one or more instead.
 * • Return target nonland permanent to its owner's hand.
 * • Target opponent discards a card.
 * • Look at the top three cards of your library. Put one of them into your hand and the rest into
 *   your graveyard.
 *
 * Descend 8 raises the modal count as a cast-time `dynamicChooseCount`, the same shape as Molten
 * Collapse's descend gate (which itself follows the Flame of Anor pattern). The floor stays
 * `minChooseCount = 1` ("choose one" is mandatory); the cap rises to all three distinct modes when
 * there are eight or more permanent cards in your graveyard as you cast the spell
 * ([Conditions.CardsInGraveyardMatchingAtLeast] with [GameObjectFilter.Permanent] — descend 8 is
 * an ability word, CR 207.2c, with no rules entry of its own), otherwise it stays 1. Evaluated
 * against the game state at cast time by
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler].
 *
 * Mode 3 ("look at the top three ... put one into your hand and the rest into your graveyard") is
 * the standard dig recipe [Patterns.Library.lookAtTopAndKeep].
 */
val WailOfTheForgotten = card("Wail of the Forgotten") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Descend 8 — Choose one. If there are eight or more permanent cards in your " +
        "graveyard as you cast this spell, choose one or more instead.\n" +
        "• Return target nonland permanent to its owner's hand.\n" +
        "• Target opponent discards a card.\n" +
        "• Look at the top three cards of your library. Put one of them into your hand and the " +
        "rest into your graveyard."

    spell {
        modal(
            chooseCount = 3,
            minChooseCount = 1,
            dynamicChooseCount = DynamicAmount.Conditional(
                condition = Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent),
                ifTrue = DynamicAmount.Fixed(3),
                ifFalse = DynamicAmount.Fixed(1)
            )
        ) {
            mode("Return target nonland permanent to its owner's hand") {
                val permanent = target("target nonland permanent", Targets.NonlandPermanent)
                effect = Effects.ReturnToHand(permanent)
            }
            mode("Target opponent discards a card") {
                val opponent = target("target opponent", TargetOpponent())
                effect = Patterns.Hand.discardCards(1, opponent)
            }
            mode("Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard") {
                effect = Patterns.Library.lookAtTopAndKeep(count = 3, keepCount = 1)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "244"
        artist = "Ryan Valle"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67108256-b7da-44bd-9639-4264931d348f.jpg?1782694417"
    }
}
