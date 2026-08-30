package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Profound Journey
 * {5}{W}{W}
 * Sorcery
 *
 * Return target permanent card from your graveyard to the battlefield.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * [TargetFilter.PermanentInYourGraveyard] carries both halves of the printed noun — a permanent
 * card, owned by you, in the graveyard zone — and the move repeats that zone as `fromZone` so the
 * effect fails closed if the card has already left the graveyard in response. Rebound is the bare
 * [Keyword.REBOUND]; the second cast picks a fresh target on your next upkeep.
 */
val ProfoundJourney = card("Profound Journey") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return target permanent card from your graveyard to the battlefield.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.PermanentInYourGraveyard))
        effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Tomasz Jedruszek"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd2dcfe8-ced4-44f2-8268-68035a4d4d58.jpg?1783938614"
    }
}
