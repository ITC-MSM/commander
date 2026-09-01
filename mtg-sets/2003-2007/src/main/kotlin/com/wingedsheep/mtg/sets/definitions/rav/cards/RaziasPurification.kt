package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Razia's Purification
 * {4}{R}{W}
 * Sorcery
 * Each player chooses three permanents they control, then sacrifices the rest.
 *
 * Global Ruin's shape: inside a [ForEachPlayerEffect] each player gathers the permanents they
 * control, keeps a selection, and sacrifices the remainder. Two details the oracle text pins down:
 *
 * - The keep is [SelectionMode.ChooseExactly] three, not "up to three". A player who wants to keep
 *   fewer doesn't get to — and [SelectionMode.ChooseExactly]'s clamp is exactly right for the
 *   2005-10-01 ruling "if a player doesn't control three permanents, that player chooses all the
 *   permanents they do control and doesn't sacrifice anything": with two permanents the executor
 *   selects both and the remainder is empty.
 * - `Player.Each` walks the table in turn order, and the sacrifice for each player runs as that
 *   player's own step. The second 2005-10-01 ruling wants the choices in turn order and then a
 *   single simultaneous sacrifice; the engine's per-player fold makes the sacrifices sequential
 *   instead. The distinction is only observable through leaves-the-battlefield triggers that count
 *   other permanents mid-resolution, and no other card in this shape (Global Ruin included) models
 *   it differently.
 *
 * "Permanents they control" is every permanent, lands and tokens included — [GameObjectFilter]'s
 * bare `Permanent`, with `Player.You` inside the loop meaning the player being processed.
 */
val RaziasPurification = card("Razia's Purification") {
    manaCost = "{4}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Sorcery"
    oracleText = "Each player chooses three permanents they control, then sacrifices the rest."

    spell {
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                GatherCardsEffect(
                    source = CardSource.ControlledPermanents(
                        player = Player.You,
                        filter = GameObjectFilter.Permanent
                    ),
                    storeAs = "permanents"
                ),
                SelectFromCollectionEffect(
                    from = "permanents",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(3)),
                    chooser = Chooser.Controller,
                    storeSelected = "kept",
                    storeRemainder = "sacrificed",
                    selectedLabel = "Keep",
                    remainderLabel = "Sacrifice",
                    prompt = "Choose three permanents you control to keep; the rest are sacrificed.",
                    useTargetingUI = true,
                    alwaysPrompt = true
                ),
                MoveCollectionEffect(
                    from = "sacrificed",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    moveType = MoveType.Sacrifice
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Shishizaru"
        flavorText = "Only the chosen ones are spared."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73bfefd3-bddd-47bb-92f3-9356a7bca637.jpg?1783943615"
        ruling(
            "2005-10-01",
            "If a player doesn't control three permanents, that player chooses all the permanents " +
                "they do control and doesn't sacrifice anything."
        )
        ruling(
            "2005-10-01",
            "Players choose permanents in turn order around the table, then simultaneously " +
                "sacrifice all permanents not chosen."
        )
    }
}
