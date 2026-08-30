package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Empty the Catacombs
 * {3}{B}
 * Sorcery
 * Each player returns all creature cards from their graveyard to their hand.
 *
 * Twilight's Call's symmetric gather, landing in hand instead of on the battlefield: one
 * [GatherCardsEffect] over every graveyard ([Player.Each]) filtered to creature cards, then a
 * [MoveCollectionEffect] to [Zone.HAND] — cards moved to hand always go to their *owner's* hand,
 * so no ownership rebinding is needed.
 */
val EmptyTheCatacombs = card("Empty the Catacombs") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Each player returns all creature cards from their graveyard to their hand."
    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.Each,
                        filter = GameObjectFilter.Creature,
                    ),
                    storeAs = "graveyardCreatures",
                ),
                MoveCollectionEffect(
                    from = "graveyardCreatures",
                    destination = CardDestination.ToZone(Zone.HAND),
                ),
            ),
        )
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "86"
        artist = "Mark A. Nelson"
        flavorText = "When the dead are laid to rest in Ravnica, it's usually just a nap."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e41cbd0b-9f54-4e8f-9a4b-fed8e435a2e0.jpg"
    }
}
