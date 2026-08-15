package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Raise the Past
 * {2}{W}{W}
 * Sorcery
 *
 * Return all creature cards with mana value 2 or less from your graveyard to the battlefield.
 *
 * A mass reanimation: gather every creature card in your graveyard with mana value 2 or less,
 * then move the whole collection onto the battlefield under their owner's control. No choice or
 * targeting — the whole matching set returns.
 */
val RaiseThePast = card("Raise the Past") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return all creature cards with mana value 2 or less from your graveyard to the battlefield."

    spell {
        effect = Effects.Pipeline {
            val creatures = gather(
                CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.You,
                    filter = GameObjectFilter.Creature.manaValueAtMost(2),
                ),
                name = "graveyardCreatures",
            )
            move(
                creatures,
                CardDestination.ToZone(Zone.BATTLEFIELD),
                underOwnersControl = true,
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Nathaniel Himawan"
        flavorText = "At Strixhaven University, learning about the past means talking to those who were there."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c6be129-56da-4fe7-a6bd-6a1d402c09e1.jpg?1782689246"
    }
}
