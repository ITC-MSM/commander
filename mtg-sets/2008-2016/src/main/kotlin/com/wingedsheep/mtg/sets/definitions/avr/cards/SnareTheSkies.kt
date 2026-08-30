package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Snare the Skies
 * {G}
 * Instant
 *
 * Target creature gets +1/+1 and gains reach until end of turn. (It can block creatures with flying.)
 *
 * One target, two clauses: the pump and the keyword grant both bind to it and both take the
 * default until-end-of-turn duration.
 */
val SnareTheSkies = card("Snare the Skies") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+1 and gains reach until end of turn. (It can block creatures with " +
        "flying.)"

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, creature),
            Effects.GrantKeyword(Keyword.REACH, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Ryan Yee"
        flavorText = "A hunter's precision and a durkvine's strength are a potent combination."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28f75827-a144-4fe2-a713-4439ae7567eb.jpg?1783940662"
    }
}
