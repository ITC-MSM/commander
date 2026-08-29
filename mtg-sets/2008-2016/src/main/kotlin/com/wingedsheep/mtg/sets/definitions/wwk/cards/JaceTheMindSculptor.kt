package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Jace, the Mind Sculptor
 * {2}{U}{U}
 * Legendary Planeswalker — Jace
 * Starting Loyalty: 3
 *
 * +2: Look at the top card of target player's library. You may put that card on the bottom of that
 *     player's library.
 * 0: Draw three cards, then put two cards from your hand on top of your library in any order.
 * −1: Return target creature to its owner's hand.
 * −12: Exile all cards from target player's library, then that player shuffles their hand into
 *      their library.
 *
 * Three of the four abilities reach into a *targeted* player's zones rather than the controller's,
 * so every zone reference in them is [Player.ContextPlayer] `(0)` — the target bound by
 * `target("target player", Targets.Player)` — and only the look itself stays with the controller
 * (the default [com.wingedsheep.sdk.scripting.effects.LookAudience.Controller] on `gather`).
 *
 * The 0 is Brainstorm's shape verbatim (draw then Gather → Select → Move over hand), and for the
 * same reason: one atomic resolution, no priority window between the draw and the put-back
 * (2020-08-07 ruling).
 *
 * The −12's put-back is one [ZonePlacement.Shuffled] move of the whole hand. An empty hand gathers
 * nothing, so the move is a no-op and no shuffle happens — the second 2020-08-07 ruling, where the
 * target's library stays empty and they lose only on their next draw.
 */
val JaceTheMindSculptor = card("Jace, the Mind Sculptor") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Jace"
    startingLoyalty = 3
    oracleText = "+2: Look at the top card of target player's library. You may put that card on " +
        "the bottom of that player's library.\n" +
        "0: Draw three cards, then put two cards from your hand on top of your library in any order.\n" +
        "−1: Return target creature to its owner's hand.\n" +
        "−12: Exile all cards from target player's library, then that player shuffles their hand " +
        "into their library."

    loyaltyAbility(+2) {
        target("target player", Targets.Player)
        effect = Effects.Pipeline {
            val top = gather(
                CardSource.TopOfLibrary(
                    count = DynamicAmount.Fixed(1),
                    player = Player.ContextPlayer(0)
                )
            )
            val toBottom = chooseUpTo(
                count = 1,
                from = top,
                prompt = "Put that card on the bottom of that player's library?",
                selectedLabel = "Put on the bottom of that player's library"
            )
            toLibraryBottom(toBottom, player = Player.ContextPlayer(0))
        }
    }

    loyaltyAbility(0) {
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.Pipeline {
                val hand = gather(CardSource.FromZone(Zone.HAND, Player.You, GameObjectFilter.Any))
                val putBack = chooseExactly(
                    count = 2,
                    from = hand,
                    prompt = "Put two cards from your hand on top of your library"
                )
                toLibraryTop(putBack)
            }
        )
    }

    loyaltyAbility(-1) {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ReturnToHand(creature)
    }

    loyaltyAbility(-12) {
        target("target player", Targets.Player)
        effect = Effects.Pipeline {
            val library = gather(
                CardSource.FromZone(Zone.LIBRARY, Player.ContextPlayer(0), GameObjectFilter.Any)
            )
            exile(library, owner = Player.ContextPlayer(0))
            val hand = gather(
                CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0), GameObjectFilter.Any)
            )
            move(
                hand,
                CardDestination.ToZone(Zone.LIBRARY, Player.ContextPlayer(0), ZonePlacement.Shuffled)
            )
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "31"
        artist = "Jason Chan"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e606072-a3aa-4300-ba90-ec92a721fa76.jpg?1783942061"

        ruling(
            "2020-08-07",
            "You draw three cards and put two cards back all while Jace's second ability is " +
                "resolving. Nothing can happen between the two, and no player may choose to take actions."
        )
        ruling(
            "2020-08-07",
            "If the target player for Jace's last ability has no cards in hand, that player " +
                "shuffles nothing into their library, and that player's library will remain empty. " +
                "That player won't lose the game until they try to draw from the empty library."
        )
    }
}
