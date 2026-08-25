package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
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
 * Splendid Reclamation — Eldritch Moon #171
 * {3}{G} · Sorcery
 *
 * Return all land cards from your graveyard to the battlefield tapped.
 *
 * The Aftermath Analyst / Lumra, Bellow of the Woods shape as a bare spell: gather every land card
 * in the controller's graveyard into one collection, then move that whole collection to the
 * battlefield with [ZonePlacement.Tapped]. Gathering first is what makes "all land cards" a single
 * snapshot taken on resolution — nothing is re-scanned between the individual moves, so a land that
 * somehow leaves the graveyard mid-move can't be returned twice and one that arrives mid-move isn't
 * picked up.
 *
 * "Return … to the battlefield" is not targeted, so nothing here is chosen at cast time and the
 * spell resolves even if the graveyard is empty (a no-op). The returned lands enter under their
 * *owner's* control by default — which for a graveyard is the same player as the controller here,
 * since a card in a graveyard is always in its owner's graveyard (CR 404.1).
 */
val SplendidReclamation = card("Splendid Reclamation") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Return all land cards from your graveyard to the battlefield tapped."

    spell {
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
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Wesley Burt"
        flavorText = "\"No matter how much a plane has suffered, there is a way to restore it.\"\n—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e17e172-3a46-4957-b711-edc53f70a284.jpg?1783937436"
    }
}
