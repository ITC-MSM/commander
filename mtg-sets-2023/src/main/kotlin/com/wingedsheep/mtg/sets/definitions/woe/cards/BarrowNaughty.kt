package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Barrow Naughty
 * {1}{B}
 * Creature — Faerie
 * 1/3
 *
 * Flying
 * This creature has lifelink as long as you control another Faerie.
 * {2}{B}: This creature gets +1/+0 until end of turn.
 *
 * The conditional lifelink is a continuous static that grants the keyword to the source only while
 * you control a *different* Faerie (excludeSelf), mirroring Magitek Infantry's "another artifact"
 * shape. Pump is a plain self-targeted stat mod (default until-end-of-turn duration).
 */
val BarrowNaughty = card("Barrow Naughty") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie"
    power = 1
    toughness = 3
    oracleText = "Flying\nThis creature has lifelink as long as you control another Faerie.\n" +
        "{2}{B}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.LIFELINK, GroupFilter.source()),
            condition = Conditions.YouControl(
                GameObjectFilter.Creature.withSubtype(Subtype.FAERIE),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{2}{B}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Matt Forsyth"
        flavorText = "\"The difference between mischief and malice? Numbers.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67da5ad8-4de2-4bd4-8b95-f2657e1fdee5.jpg?1783915111"
    }
}
