package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Call the Bloodline (Shadows over Innistrad #103)
 * {1}{B}
 * Enchantment
 *
 * {1}, Discard a card: Create a 1/1 black Vampire Knight creature token with lifelink.
 * Activate only once each turn.
 *
 * "Activate only once each turn" is [ActivationRestriction.OncePerTurn] — a restriction on the
 * ability, not a condition on the effect. The discard is part of the *cost*, so it is paid on
 * activation (and enables madness on the discarded card).
 */
val CallTheBloodline = card("Call the Bloodline") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "{1}, Discard a card: Create a 1/1 black Vampire Knight creature token with lifelink. Activate only once each turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.DiscardCard)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Vampire", "Knight"),
            keywords = setOf(Keyword.LIFELINK)
        )
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "103"
        artist = "Lake Hurwitz"
        flavorText = "At Sorin's appeal, Olivia Voldaren summoned the full might of her bloodline to gather at Lurenbraum Fortress."
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3ec85cca-ac2c-4b1b-850b-6a762df72bd0.jpg?1783937778"
    }
}
