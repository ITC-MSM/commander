package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind Dancer
 * {1}{U}
 * Creature — Faerie
 * 1/1
 * Flying
 * {T}: Target creature gains flying until end of turn.
 */
val WindDancer = card("Wind Dancer") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "{T}: Target creature gains flying until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
        description = "{T}: Target creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Susan Van Camp"
        flavorText = "\"Flying like a bird does not make you as free as one.\"\n" +
            "—Volrath"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea7f7a94-700a-4f3b-846c-a36505b80875.jpg"
    }
}
