package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Snowmelt Stag — Tarkir: Dragonstorm #57
 * {3}{U} · Creature — Elemental Elk · 2/5
 *
 * Vigilance
 * During your turn, this creature has base power and toughness 5/2.
 * {5}{U}{U}: This creature can't be blocked this turn.
 *
 * The "during your turn" base P/T swap is a [SetBasePowerToughnessStatic] on this
 * creature itself (Layer 7b — set base power/toughness), gated behind
 * [Conditions.IsYourTurn] via a conditional static ability so it only applies on the
 * controller's turn. The printed 2/5 is the off-turn base; the static overrides it to
 * 5/2 during your turn.
 */
val SnowmeltStag = card("Snowmelt Stag") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Elk"
    power = 2
    toughness = 5
    oracleText = "Vigilance\n" +
        "During your turn, this creature has base power and toughness 5/2.\n" +
        "{5}{U}{U}: This creature can't be blocked this turn."

    keywords(Keyword.VIGILANCE)

    // During your turn: base power/toughness 5/2.
    staticAbility {
        ability = SetBasePowerToughnessStatic(power = 5, toughness = 2, filter = GroupFilter.source())
        condition = Conditions.IsYourTurn
    }

    // {5}{U}{U}: This creature can't be blocked this turn.
    activatedAbility {
        cost = Costs.Mana("{5}{U}{U}")
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Lorenzo Mastroianni"
        flavorText = "Elementals conjured by Temur mages are forever watchful and swift."
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6b3b131-704a-4586-84f8-db465cd4a277.jpg?1743204189"
    }
}
