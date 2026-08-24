package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Amphin Pathmage
 * {3}{U}
 * Creature — Salamander Wizard
 * 3/2
 * {2}{U}: Target creature can't be blocked this turn.
 */
val AmphinPathmage = card("Amphin Pathmage") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Salamander Wizard"
    power = 3
    toughness = 2
    oracleText = "{2}{U}: Target creature can't be blocked this turn."

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Mark Winters"
        flavorText = "\"There are those who do not believe in the existence of the amphin. This seems somehow to be of their own design.\"\n—Gor Muldrak, *Cryptohistories*"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eafd2e27-01d0-4894-886e-2b8776904ab9.jpg?1783939194"
    }
}
