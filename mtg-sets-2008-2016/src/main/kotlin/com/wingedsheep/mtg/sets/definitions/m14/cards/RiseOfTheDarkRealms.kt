package com.wingedsheep.mtg.sets.definitions.m14.cards

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
 * Rise of the Dark Realms
 * {7}{B}{B}
 * Sorcery
 *
 * Put all creature cards from all graveyards onto the battlefield under your control.
 *
 * `Player.Each` gathers every player's graveyard at once, so this sweeps up your own dead
 * as well as your opponents'. The moved cards enter the battlefield under *your* control:
 * `CardDestination.ToZone` defaults its `player` to [Player.You] (the spell's controller) and
 * `underOwnersControl` stays false, so the caster — not each card's owner — controls them.
 */
val RiseOfTheDarkRealms = card("Rise of the Dark Realms") {
    manaCost = "{7}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Put all creature cards from all graveyards onto the battlefield under your control."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.Each,
                        filter = GameObjectFilter.Creature
                    ),
                    storeAs = "creatureCardsInAllGraveyards"
                ),
                MoveCollectionEffect(
                    from = "creatureCardsInAllGraveyards",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "111"
        artist = "Michael Komarck"
        flavorText = "\"For every living person there are generations of dead. Which realm would you rather rule?\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/073f81e8-8c0c-4430-bd3e-95ed3625340f.jpg?1783939921"
    }
}
