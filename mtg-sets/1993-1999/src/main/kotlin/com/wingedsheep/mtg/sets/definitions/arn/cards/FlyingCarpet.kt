package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Flying Carpet
 * {4}
 * Artifact
 * {2}, {T}: Target creature gains flying until end of turn.
 */
val FlyingCarpet = card("Flying Carpet") {
    manaCost = "{4}"
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Target creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b71ff49-ee0a-4065-9131-380468d62a30.jpg?1562908777"
    }
}
