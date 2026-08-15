package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Drafna's Restoration
 * {U}
 * Sorcery
 * Put any number of target artifact cards from target player's graveyard on top of their library
 * in any order.
 *
 * Two separate targets: a player (index 0), then *any number* of artifact cards in that player's
 * graveyard. The graveyard-card target's filter is `GameObjectFilter.Artifact.ownedByTargetPlayer()`
 * in `Zone.GRAVEYARD` (reusing the Hurkyl's-Recall `OwnedByTargetPlayer` predicate). The engine's
 * `TargetValidator` now threads the chosen player target into graveyard-card validation, so
 * `OwnedByTargetPlayer` resolves "target player's graveyard".
 *
 * The chosen cards are gathered ([CardSource.ChosenTargets]) and moved to the top of the owner's
 * library with [CardOrder.ControllerChooses] — the caster orders them when more than one is chosen.
 */
val DrafnasRestoration = card("Drafna's Restoration") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Put any number of target artifact cards from target player's graveyard on top of " +
        "their library in any order."

    spell {
        target("target player", Targets.Player)
        target(
            "any number of target artifact cards from that player's graveyard",
            TargetObject(
                unlimited = true,
                filter = TargetFilter(
                    GameObjectFilter.Artifact.ownedByTargetPlayer(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.Composite(
            GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "drafna_cards"),
            MoveCollectionEffect(
                from = "drafna_cards",
                destination = CardDestination.ToZone(
                    Zone.LIBRARY,
                    player = Player.TargetPlayer,
                    placement = ZonePlacement.Top
                ),
                order = CardOrder.ControllerChooses
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Amy Weber"
        flavorText = "Drafna, founder of the College of Lat-Nam, could create a working model from " +
            "even the smallest remnants of a newly unearthed artifact."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4be2aa3b-207b-4d21-abfb-6788520c7676.jpg?1562910905"
    }
}
