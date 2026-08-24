package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Assault Strobe
 * {R}
 * Sorcery
 *
 * Target creature gains double strike until end of turn. (It deals both first-strike and regular combat damage.)
 */
val AssaultStrobe = card("Assault Strobe") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Target creature gains double strike until end of turn. (It deals both first-strike and regular combat damage.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Kev Walker"
        flavorText = "When breaking someone's face once just isn't enough."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b505c78-5dbd-483d-92bb-5144060e962f.jpg?1783941727"
    }
}
