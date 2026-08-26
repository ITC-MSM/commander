package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Caller of Gales
 * {U}
 * Creature — Merfolk Wizard
 * 1/1
 * {1}{U}, {T}: Target creature gains flying until end of turn.
 */
val CallerOfGales = card("Caller of Gales") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "{1}{U}, {T}: Target creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        val creature = target("creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Some merfolk choose to rest their fins in the water. I believe wisdom exists not only where we were born but where we were told not to go.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5aabef2-982b-42c9-9804-08ce4d8910cd.jpg"
    }
}
