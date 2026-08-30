package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shape the Sands
 * {G}
 * Instant
 *
 * Target creature gets +0/+5 and gains reach until end of turn. (It can block creatures with flying.)
 *
 * The Dromoka answer to a flying dragon: one [Effects.Composite] over the toughness pump and the
 * reach grant, both bound to the same target. Nothing restricts the target's controller, so the
 * bare [Targets.Creature] requirement is correct — you can shore up an opponent's blocker if you
 * ever want to.
 */
val ShapeTheSands = card("Shape the Sands") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +0/+5 and gains reach until end of turn. (It can block creatures with flying.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(0, 5, t),
            Effects.GrantKeyword(Keyword.REACH, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Ryan Yee"
        flavorText = "\"Dragons in flight seldom expect company.\"\n—Kadri, Dromoka warrior"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0243dde4-29c9-4e47-9129-8e01296851cc.jpg?1783938576"
    }
}
