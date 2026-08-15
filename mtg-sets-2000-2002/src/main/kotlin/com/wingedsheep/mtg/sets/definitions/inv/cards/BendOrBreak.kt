package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ChoosePileEffect
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TapUntapCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bend or Break
 * {3}{R}
 * Sorcery
 * Each player separates all nontoken lands they control into two piles. For each
 * player, one of their piles is chosen by one of their opponents of their choice.
 * Destroy all lands in the chosen piles. Tap all lands in the other piles.
 *
 * A per-player "divvy" (CR 700.3): each player partitions their own lands, then an
 * opponent of that player chooses which pile dies. Composed from the pile primitives
 * inside [ForEachPlayerEffect] — within the loop the iterated player is the controller
 * (so they separate and `Chooser.Opponent` resolves to *their* opponent). Each player's
 * separate → choose → destroy/tap runs in turn order (the engine is two-player in
 * practice, where simultaneity is moot).
 */
val BendOrBreak = card("Bend or Break") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Each player separates all nontoken lands they control into two piles. For each player, one of their piles is chosen by one of their opponents of their choice. Destroy all lands in the chosen piles. Tap all lands in the other piles."

    spell {
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                // 1. Gather the nontoken lands this player controls.
                GatherCardsEffect(
                    source = CardSource.ControlledPermanents(
                        player = Player.You,
                        filter = GameObjectFilter.Land.nontoken()
                    ),
                    storeAs = "lands"
                ),
                // 2. This player separates their lands into two piles.
                SelectFromCollectionEffect(
                    from = "lands",
                    selection = SelectionMode.ChooseAnyNumber,
                    chooser = Chooser.Controller,
                    storeSelected = "pileA",
                    storeRemainder = "pileB",
                    selectedLabel = "Pile 1",
                    remainderLabel = "Pile 2",
                    prompt = "Separate your lands into two piles. The lands you select form Pile 1; the rest form Pile 2.",
                    useTargetingUI = true,
                    alwaysPrompt = true
                ),
                // 3. An opponent chooses one of this player's piles to be destroyed.
                ChoosePileEffect(
                    pileA = "pileA",
                    pileB = "pileB",
                    pileALabel = "Pile 1",
                    pileBLabel = "Pile 2",
                    chooser = Chooser.Opponent,
                    storeChosenAs = "destroyed",
                    storeOtherAs = "tapped",
                    prompt = "Choose which of the player's land piles is destroyed; the other pile is tapped."
                ),
                // 4. Destroy the chosen pile.
                MoveCollectionEffect(
                    from = "destroyed",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    moveType = MoveType.Destroy
                ),
                // 5. Tap the other pile.
                TapUntapCollectionEffect(collectionName = "tapped", tap = true)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "137"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b76b6660-d4b2-44de-a1a7-8d00811f90f6.jpg?1562931926"
    }
}
