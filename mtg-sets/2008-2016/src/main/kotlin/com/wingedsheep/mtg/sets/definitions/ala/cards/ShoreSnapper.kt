package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shore Snapper
 * {2}{B}
 * Creature — Beast
 * 2 / 2
 * {U}: This creature gains islandwalk until end of turn. (It can't be blocked as long as defending player controls an Island.)
 *
 * The Deeptread Merrow shape: a bare off-colour [Costs.Mana] activation whose effect is
 * [Effects.GrantKeyword] of [Keyword.ISLANDWALK] on [EffectTarget.Self]. The grant's default
 * `Duration.EndOfTurn` is the printed "until end of turn"; the landwalk evasion itself is read by
 * the blocker-legality check, so no separate can't-be-blocked rider is needed.
 */
val ShoreSnapper = card("Shore Snapper") {
    manaCost = "{2}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "{U}: This creature gains islandwalk until end of turn. (It can't be blocked as long as defending player controls an Island.)"

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.GrantKeyword(Keyword.ISLANDWALK, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Dave Kendall"
        flavorText = "Kathari, the sickly aven of Grixis, have learned that the corpses by the shoreline are more trap than treat."
        imageUri = "https://cards.scryfall.io/normal/front/1/5/157e5763-4892-47e4-8fd5-f576844c0a0d.jpg"
    }
}
