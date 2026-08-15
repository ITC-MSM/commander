package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Memorial to War
 * Land
 * Memorial to War enters the battlefield tapped.
 * {T}: Add {R}.
 * {4}{R}, {T}, Sacrifice Memorial to War: Destroy target land.
 */
val MemorialToWar = card("Memorial to War") {
    typeLine = "Land"
    colorIdentity = "R"
    oracleText = "Memorial to War enters the battlefield tapped.\n{T}: Add {R}.\n{4}{R}, {T}, Sacrifice Memorial to War: Destroy target land."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{R}"), Costs.Tap, Costs.SacrificeSelf)
        val land = target("target land", TargetObject(filter = TargetFilter(GameObjectFilter.Land)))
        effect = Effects.Destroy(land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Richard Wright"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53920ed7-fc9a-4b50-85c8-d62de05b2390.jpg?1562735726"
    }
}
