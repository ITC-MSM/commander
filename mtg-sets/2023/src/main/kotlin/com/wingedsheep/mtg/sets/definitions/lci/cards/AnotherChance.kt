package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Another Chance
 * {2}{B}
 * Instant — Common (LCI #90)
 *
 * "You may mill two cards. Then return up to two creature cards from your graveyard to your hand."
 *
 * Implementation:
 *  - [MayEffect] wrapping [Patterns.Library.mill] handles the optional mill of 2 cards; resolving
 *    with "no" skips the mill and proceeds to the return step.
 *  - Gather creature cards from the controller's graveyard, [SelectionMode.ChooseUpTo](2), then
 *    move selected cards to hand. Choosing 0 is valid ("up to two" = 0–2). An empty graveyard
 *    auto-skips the selection prompt (per [SelectFromCollectionExecutor] short-circuit).
 */
val AnotherChance = card("Another Chance") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "You may mill two cards. Then return up to two creature cards from your graveyard to your hand. (To mill two cards, put the top two cards of your library into your graveyard.)"

    spell {
        effect = Effects.Composite(
            listOf(
                // "You may mill two cards."
                MayEffect(Patterns.Library.mill(2)),
                // "Then return up to two creature cards from your graveyard to your hand."
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.Creature),
                    storeAs = "graveyardCreatures"
                ),
                SelectFromCollectionEffect(
                    from = "graveyardCreatures",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
                    storeSelected = "chosen",
                    showAllCards = true,
                    prompt = "Return up to two creature cards from your graveyard to your hand",
                    selectedLabel = "Return to hand",
                    remainderLabel = "Leave in graveyard"
                ),
                MoveCollectionEffect(
                    from = "chosen",
                    destination = CardDestination.ToZone(Zone.HAND)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20d9eb9f-2fcc-49f0-99c1-bad748239466.jpg?1782694539"
    }
}
