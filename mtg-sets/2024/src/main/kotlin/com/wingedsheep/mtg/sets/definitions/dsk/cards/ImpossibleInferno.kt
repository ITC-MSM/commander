package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Impossible Inferno
 * {4}{R}
 * Instant
 * Impossible Inferno deals 6 damage to target creature.
 * Delirium — If there are four or more card types among cards in your graveyard, exile the
 * top card of your library. You may play it until the end of your next turn.
 */
val ImpossibleInferno = card("Impossible Inferno") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Impossible Inferno deals 6 damage to target creature.\n" +
        "Delirium — If there are four or more card types among cards in your graveyard, exile the top card of your library. You may play it until the end of your next turn."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            DealDamageEffect(6, t),
            // Delirium: only exile/grant-play if there are four or more card types in your graveyard.
            ConditionalEffect(
                condition = Conditions.Delirium(),
                effect = Effects.Composite(
                    GatherCardsEffect(
                        source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                        storeAs = "impulseExiled"
                    ),
                    MoveCollectionEffect(
                        from = "impulseExiled",
                        destination = CardDestination.ToZone(Zone.EXILE)
                    ),
                    GrantMayPlayFromExileEffect("impulseExiled", MayPlayExpiry.UntilEndOfNextTurn)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "No living being, survivor or monster, is exempt from Valgavoth's wrath."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a35248f9-9a4e-4758-a4c1-0e0c83e3fd75.jpg?1726286368"
    }
}
