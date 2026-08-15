package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Mind Roots
 * {1}{B}{G}
 * Sorcery
 *
 * Target player discards two cards. Put up to one land card discarded this way onto the
 * battlefield tapped under your control.
 *
 * One inline [Effects.Pipeline]: gather the target player's hand and have *them* choose exactly two
 * to discard ([Chooser.TargetPlayer], [MoveType.Discard] → their graveyard). The selected slot keeps
 * referencing those two cards after they land in the graveyard, so we [filter] it down to land cards,
 * let *you* (the controller) pick up to one of those, and move it to the battlefield tapped under your
 * control. If neither discarded card is a land, the land filter empties and the up-to-one selection is
 * a no-op — matching "up to one".
 */
val MindRoots = card("Mind Roots") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Target player discards two cards. Put up to one land card discarded this way " +
        "onto the battlefield tapped under your control."

    spell {
        val player = target("player", TargetPlayer())
        effect = Effects.Pipeline {
            // Target player discards two cards (they choose).
            val hand = gather(CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)), name = "hand")
            val discarded = chooseExactly(
                2, from = hand,
                chooser = Chooser.TargetPlayer,
                prompt = "Choose two cards to discard",
                name = "discarded"
            )
            moveTracked(
                discarded,
                CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                moveType = MoveType.Discard,
                name = "discardedToGrave"
            )
            // Of the cards discarded this way, you may put up to one land onto the battlefield
            // tapped under your control.
            val discardedLands = filter(discarded, GameObjectFilter.Land, name = "discardedLands")
            val chosenLand = chooseUpTo(
                1, from = discardedLands,
                prompt = "Put up to one discarded land onto the battlefield tapped",
                name = "chosenLand"
            )
            move(
                chosenLand,
                CardDestination.ToZone(Zone.BATTLEFIELD, Player.You, ZonePlacement.Tapped)
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Elliot Lang"
        flavorText = "The best ideas stem from dreams."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d5fdbda-ebbe-45d6-a668-5ddee057a063.jpg?1775938410"
    }
}
