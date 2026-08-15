package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration

/**
 * Slimy Kavu
 * {2}{R}
 * Creature — Kavu
 * 2/2
 * {T}: Target land becomes a Swamp until end of turn.
 *
 * "Becomes" replaces the land's existing land subtypes (Rule 305.7), so the land loses
 * the mana abilities of its old types and gains Swamp's — modeled with
 * [Effects.SetLandType] rather than the additive [Effects.AddSubtype]. Same shape as
 * Dream Thrush, but with a fixed type instead of a player choice.
 */
val SlimyKavu = card("Slimy Kavu") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 2
    oracleText = "{T}: Target land becomes a Swamp until end of turn."

    activatedAbility {
        val land = target("target land", Targets.Land)
        cost = AbilityCost.Tap
        effect = Effects.SetLandType(
            landType = "Swamp",
            target = land,
            duration = Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Randy Gallegos"
        flavorText = "Its slime liquefies the ground as efficiently as its fangs shred prey."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e82044d-88cd-4ee4-8ec9-e71a0a85ed46.jpg?1562923768"
    }
}
