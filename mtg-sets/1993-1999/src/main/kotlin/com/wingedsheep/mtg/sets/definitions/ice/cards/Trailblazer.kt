package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trailblazer
 * {2}{G}{G}
 * Instant
 *
 * Target creature can't be blocked this turn.
 *
 * "Can't be blocked" is an [AbilityFlag], not a CR 702 keyword — it names a whole sentence rather
 * than a noun a creature can be said to gain — so this is the ordinary until-end-of-turn keyword
 * grant over the flag, the same effect "gains flying until end of turn" builds.
 */
val Trailblazer = card("Trailblazer") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature can't be blocked this turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "272"
        artist = "Julie Baroh"
        flavorText = "\"Our Elvish Hunter Taaveti led us swiftly along hidden paths through the dense forest. We caught the Orcs from behind, and completely by surprise.\"\n—Lucilde Fiksdotter, Leader of the Order of the White Shield"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9194c69d-c849-4c4a-976c-d1382bd5cf32.jpg"
    }
}
