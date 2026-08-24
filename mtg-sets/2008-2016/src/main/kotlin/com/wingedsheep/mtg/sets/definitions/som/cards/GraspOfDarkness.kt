package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grasp of Darkness
 * {B}{B}
 * Instant
 *
 * Target creature gets -4/-4 until end of turn.
 */
val GraspOfDarkness = card("Grasp of Darkness") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-4 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-4, -4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Johann Bodin"
        flavorText = "On a world with five suns, night is compelled to become an aggressive force."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cda628ba-19f4-4e24-9500-cca295a992bb.jpg?1783941730"
    }
}
