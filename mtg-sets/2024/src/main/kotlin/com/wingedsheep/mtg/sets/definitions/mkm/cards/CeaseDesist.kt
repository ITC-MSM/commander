package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Cease // Desist — Murders at Karlov Manor #246
 *
 * Cease gathers only object targets (the target player is not a card entity), so its pipeline moves
 * exactly the zero-to-two graveyard cards before applying the life gain and draw to the separately
 * captured player target. [TargetObject.sameOwner] enforces "from a single graveyard" at targeting.
 */
val CeaseDesist = card("Cease // Desist") {
    layout = CardLayout.SPLIT
    colorIdentity = "WBG"

    face("Cease") {
        manaCost = "{1}{B/G}"
        typeLine = "Instant"
        oracleText = "Exile up to two target cards from a single graveyard. Target player gains 2 life and draws a card."

        spell {
            target(
                "up to two target cards from a single graveyard",
                TargetObject(
                    count = 2,
                    optional = true,
                    filter = TargetFilter.CardInGraveyard,
                    sameOwner = true,
                ),
            )
            val player = target("target player", Targets.Player)
            effect = Effects.Composite(
                GatherCardsEffect(CardSource.ChosenTargets, "ceaseTargets"),
                MoveCollectionEffect(
                    from = "ceaseTargets",
                    destination = CardDestination.ToZone(Zone.EXILE),
                ),
                Effects.GainLife(2, player),
                Effects.DrawCards(1, player),
            )
        }
    }

    face("Desist") {
        manaCost = "{4}{G/W}{G/W}"
        typeLine = "Sorcery"
        oracleText = "Destroy all artifacts and enchantments."

        spell {
            effect = Effects.DestroyAll(
                filter = com.wingedsheep.sdk.scripting.GameObjectFilter.Artifact.or(
                    com.wingedsheep.sdk.scripting.GameObjectFilter.Enchantment,
                ),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb59130a-a134-4383-b983-e4b526d11fb4.jpg?1783912830"
    }
}
