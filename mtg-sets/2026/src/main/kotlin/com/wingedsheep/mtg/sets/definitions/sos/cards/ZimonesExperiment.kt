package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Zimone's Experiment
 * {3}{G}
 * Sorcery
 *
 * Look at the top five cards of your library. You may reveal up to two creature and/or land
 * cards from among them, then put the rest on the bottom of your library in a random order.
 * Put all land cards revealed this way onto the battlefield tapped and put all creature cards
 * revealed this way into your hand.
 *
 * Atomic Gather → Select → split-by-type → Move pipeline:
 *  1. Gather the top five cards into a private "looked" pile.
 *  2. Choose up to two creature/land cards (the remainder is everything not chosen).
 *  3. Split the chosen pile by type, routing lands → battlefield tapped, creatures → hand.
 *  4. Bottom the rest in a random order.
 *
 * A chosen pile entry can only be a creature or a land (the selection filter is
 * [GameObjectFilter.CreatureOrLand]); a card that is both a creature and a land would be put
 * onto the battlefield by the land split, matching the printed routing order.
 */
val ZimonesExperiment = card("Zimone's Experiment") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top five cards of your library. You may reveal up to two creature " +
        "and/or land cards from among them, then put the rest on the bottom of your library in a " +
        "random order. Put all land cards revealed this way onto the battlefield tapped and put " +
        "all creature cards revealed this way into your hand."

    spell {
        effect = Effects.Pipeline {
            val looked = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(5)), name = "looked")
            val (kept, rest) = chooseUpToSplit(
                count = 2,
                from = looked,
                filter = GameObjectFilter.CreatureOrLand,
                prompt = "Reveal up to two creature and/or land cards",
                showAllCards = true,
                name = "kept",
                remainderName = "rest",
            )
            val (lands, creatures) = filterSplit(
                from = kept,
                filter = GameObjectFilter.Land,
                name = "keptLands",
                restName = "keptCreatures",
            )
            move(
                from = lands,
                destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
                revealed = true,
            )
            move(
                from = creatures,
                destination = CardDestination.ToZone(Zone.HAND),
                revealed = true,
            )
            move(
                from = rest,
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random,
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "169"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6597852-4267-4ea6-a391-f927e4833be2.jpg?1775938156"
    }
}
