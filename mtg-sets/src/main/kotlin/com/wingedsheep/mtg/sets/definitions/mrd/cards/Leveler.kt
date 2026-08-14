package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Leveler — Mirrodin #195
 * {5} · Artifact Creature — Juggernaut · 10/10 · Rare
 *
 * When this creature enters, exile all cards from your library.
 *
 * Modelling notes:
 * - The exile is the whole library at once, not a count: Gather [CardSource.FromZone] over
 *   `Zone.LIBRARY` for [Player.You] with no filter, then one [MoveCollectionEffect] to exile.
 *   No selection step — every card goes, so there is nothing to choose. An already-empty
 *   library gathers an empty collection and the move is a no-op.
 * - Losing the game is *not* part of the card. Per the 2021-03-19 ruling you keep playing with
 *   an empty library and only lose when you next try to draw from it (CR 104.3c), which is the
 *   engine's ordinary empty-draw state-based action — nothing card-specific to wire.
 */
val Leveler = card("Leveler") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Juggernaut"
    power = 10
    toughness = 10
    oracleText = "When this creature enters, exile all cards from your library."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.Any),
                    storeAs = "library"
                ),
                MoveCollectionEffect(
                    from = "library",
                    destination = CardDestination.ToZone(Zone.EXILE, Player.You)
                )
            )
        )
        description = "When this creature enters, exile all cards from your library."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "195"
        artist = "Carl Critchlow"
        flavorText = "Once a century, the levelers rip through every corner of Mirrodin, " +
            "obeying the commands of an unseen master."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03ffa3c3-dd29-47eb-abf2-7951fadb5c37.jpg?1783944515"
        ruling(
            "2021-03-19",
            "You won't lose the game until you try to draw from the empty library."
        )
    }
}
