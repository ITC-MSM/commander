package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Weigh Down
 * {B}
 * Sorcery
 * As an additional cost to cast this spell, exile a creature card from your graveyard.
 * Target creature gets -3/-3 until end of turn.
 *
 * The graveyard exile is an *additional cost*, not part of the effect: it is paid on casting, so an
 * empty graveyard makes the spell uncastable rather than making it resolve for free.
 */
val WeighDown = card("Weigh Down") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, exile a creature card from your graveyard.\n" +
        "Target creature gets -3/-3 until end of turn."

    additionalCost(
        Costs.additional.ExileCards(count = 1, filter = GameObjectFilter.Creature)
    )

    spell {
        val victim = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-3, -3, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "John Di Giovanni"
        flavorText = "The draugr often drown intruders. Years later, their corpses rise from the depths to join the ranks of the Dread Marn."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6ffa795d-2211-49b0-a6df-812599758f7b.jpg"
    }
}
