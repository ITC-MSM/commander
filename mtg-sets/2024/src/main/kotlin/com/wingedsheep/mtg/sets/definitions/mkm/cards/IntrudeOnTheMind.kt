package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Intrude on the Mind
 * {3}{U}{U}
 * Instant
 * Reveal the top five cards of your library and separate them into two piles. An opponent chooses
 * one of those piles. Put that pile into your hand and the other into your graveyard. Create a 0/0
 * colorless Thopter artifact creature token with flying, then put a +1/+1 counter on it for each
 * card put into your graveyard this way.
 *
 * The pipeline keeps the opponent's chosen pile and the graveyard pile in named collections. The
 * latter remains available after its cards move, so its distinct-entity count sizes the counters
 * placed on the freshly created token through the shared CREATED_TOKENS collection.
 */
val IntrudeOnTheMind = card("Intrude on the Mind") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Reveal the top five cards of your library and separate them into two piles. An " +
        "opponent chooses one of those piles. Put that pile into your hand and the other into your " +
        "graveyard. Create a 0/0 colorless Thopter artifact creature token with flying, then put " +
        "a +1/+1 counter on it for each card put into your graveyard this way."

    spell {
        effect = Effects.Pipeline {
            val revealed = gather(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(5)),
                revealed = true,
                name = "revealed"
            )
            val separated = chooseAnyNumberSplit(
                from = revealed,
                chooser = Chooser.Controller,
                prompt = "Separate the revealed cards into two piles. The cards you select form " +
                    "Pile 1; the rest form Pile 2.",
                selectedLabel = "Pile 1",
                remainderLabel = "Pile 2",
                alwaysPrompt = true,
                name = "pileOne",
                remainderName = "pileTwo"
            )
            val chosen = choosePile(
                pileA = separated.selected,
                pileB = separated.remainder,
                chooser = Chooser.Opponent,
                prompt = "Choose a pile. That pile goes to your opponent's hand; the other goes " +
                    "to their graveyard.",
                chosenName = "handPile",
                otherName = "graveyardPile"
            )
            toHand(chosen.chosen)
            toGraveyard(chosen.other)
            run(
                Effects.CreateToken(
                    power = 0,
                    toughness = 0,
                    colors = emptySet(),
                    creatureTypes = setOf("Thopter"),
                    keywords = setOf(Keyword.FLYING),
                    artifactToken = true,
                    name = "Thopter",
                    imageUri = "https://cards.scryfall.io/normal/front/2/2/22ad97f4-cc41-493c-9848-c06f3cf778c7.jpg?1783912603"
                )
            )
            run(
                Effects.AddCountersToCollection(
                    CREATED_TOKENS,
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmount.DistinctEntitiesInCollections(listOf(chosen.other.key))
                )
            )
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "61"
        artist = "Magali Villeneuve"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbe62f47-df17-4646-88ca-89a8ec4deee9.jpg?1783912908"

        ruling(
            "2024-02-02",
            "You decide which opponent chooses the pile while resolving Intrude on the Mind."
        )
        ruling(
            "2024-02-02",
            "You may choose to put all of the cards into one pile and leave the other pile empty. " +
                "If you do, the opponent will choose whether to put the revealed cards into your " +
                "hand or your graveyard."
        )
    }
}
