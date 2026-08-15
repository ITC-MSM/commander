package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Memories Returning
 * {2}{U}{U}
 * Sorcery
 * Reveal the top five cards of your library. Put one of them into your hand. Then choose an
 * opponent. They put one on the bottom of your library. Then you put one into your hand. Then
 * they put one on the bottom of your library. Put the other into your hand.
 * Flashback {7}{U}{U}
 *
 * Modeled as a strict alternation of single picks over one revealed pile, with the remainder of
 * each selection feeding the next, so the five revealed cards are partitioned exactly: three you
 * choose go to your hand, two an opponent chooses go to the bottom of your library, and the final
 * leftover goes to your hand. The opponent is the sole opponent ([Chooser.Opponent]); in a
 * two-player game "choose an opponent" is unambiguous.
 */
val MemoriesReturning = card("Memories Returning") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Reveal the top five cards of your library. Put one of them into your hand. Then " +
        "choose an opponent. They put one on the bottom of your library. Then you put one into " +
        "your hand. Then they put one on the bottom of your library. Put the other into your hand.\n" +
        "Flashback {7}{U}{U}"

    val toHand = CardDestination.ToZone(Zone.HAND)
    val toBottom = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom)
    fun pickOne(from: String, chooser: Chooser, storeSelected: String, storeRemainder: String, prompt: String) =
        SelectFromCollectionEffect(
            from = from,
            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
            chooser = chooser,
            storeSelected = storeSelected,
            storeRemainder = storeRemainder,
            showAllCards = true,
            alwaysPrompt = true,
            prompt = prompt,
        )

    spell {
        effect = Effects.Composite(
            listOf(
                // Reveal the top five cards.
                GatherCardsEffect(source = CardSource.TopOfLibrary(DynamicAmount.Fixed(5)), storeAs = "revealed"),
                RevealCollectionEffect(from = "revealed"),
                // You put one of them into your hand.
                pickOne("revealed", Chooser.Controller, "hand1", "rem1", "Put a card into your hand"),
                MoveCollectionEffect(from = "hand1", destination = toHand),
                // An opponent puts one on the bottom of your library.
                pickOne("rem1", Chooser.Opponent, "bottom1", "rem2", "An opponent puts a card on the bottom of your library"),
                MoveCollectionEffect(from = "bottom1", destination = toBottom),
                // You put one into your hand.
                pickOne("rem2", Chooser.Controller, "hand2", "rem3", "Put a card into your hand"),
                MoveCollectionEffect(from = "hand2", destination = toHand),
                // An opponent puts one on the bottom of your library.
                pickOne("rem3", Chooser.Opponent, "bottom2", "rem4", "An opponent puts a card on the bottom of your library"),
                MoveCollectionEffect(from = "bottom2", destination = toBottom),
                // Put the other (the last remaining card) into your hand.
                MoveCollectionEffect(from = "rem4", destination = toHand),
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{7}{U}{U}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "63"
        artist = "Grace Zhu"
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a753abfc-35d3-4faf-ab35-3b51aa778174.jpg?1748705992"
    }
}
