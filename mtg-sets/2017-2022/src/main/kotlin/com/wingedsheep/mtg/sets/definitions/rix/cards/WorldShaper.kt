package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player

/**
 * World Shaper
 * {3}{G}
 * Creature — Merfolk Shaman
 * 3/3
 * Whenever this creature attacks, you may mill three cards.
 * When this creature dies, return all land cards from your graveyard to the battlefield tapped.
 *
 * The dies leg is a mass move, which has no `Patterns.*` facade — it is the hand-written
 * `GatherCardsEffect` + `MoveCollectionEffect` pair (see Aftermath Analyst).
 */
val WorldShaper = card("World Shaper") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Shaman"
    oracleText = "Whenever this creature attacks, you may mill three cards.\n" +
        "When this creature dies, return all land cards from your graveyard to the battlefield tapped."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Attacks
        optional = true
        effect = Patterns.Library.mill(3)
        description = "Whenever this creature attacks, you may mill three cards."
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.Land),
                storeAs = "graveyard_lands",
            ),
            MoveCollectionEffect(
                from = "graveyard_lands",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
            ),
        )
        description = "When this creature dies, return all land cards from your graveyard to " +
            "the battlefield tapped."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Raymond Swanland"
        flavorText = "\"The Great River will not be tamed.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d59c87f5-95ab-4385-abbe-99a3149bdbcf.jpg?1783935279"
    }
}
