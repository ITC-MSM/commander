package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.RepeatDynamicTimesEffect

/**
 * Tam, the Possibility — Reality Fracture #276
 * {1}{G}{U} · Legendary Creature — Gorgon Wizard · 2/4
 *
 * Planeswalker spells you cast cost {1} less to cast.
 * {W}{U}{B}{R}{G}, {T}: Proliferate X times, where X is the number of planeswalker types among
 * planeswalkers you control.
 *
 * X is [DynamicAmounts.planeswalkerTypes] — distinct planeswalker types (CR 205.3j), so two Jaces
 * count once — counted as the ability resolves. Each proliferate is its own choice of permanents
 * and players ([RepeatDynamicTimesEffect] over `Effects.Proliferate()`), made against the counters
 * the previous pass left.
 */
val TamThePossibility = card("Tam, the Possibility") {
    manaCost = "{1}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Gorgon Wizard"
    power = 2
    toughness = 4
    oracleText = "Planeswalker spells you cast cost {1} less to cast.\n" +
        "{W}{U}{B}{R}{G}, {T}: Proliferate X times, where X is the number of planeswalker types among " +
        "planeswalkers you control. (To proliferate, choose any number of permanents and/or players, " +
        "then give each another counter of each kind already there.)"

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Planeswalker),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{U}{B}{R}{G}"), Costs.Tap)
        effect = RepeatDynamicTimesEffect(
            amount = DynamicAmounts.planeswalkerTypes(),
            body = Effects.Proliferate()
        )
        description = "Proliferate X times, where X is the number of planeswalker types among " +
            "planeswalkers you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Martina Fačková"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6529d399-677e-45a6-ac3e-12a0b10f6c37.jpg?1789644886"
        inBooster = false
    }
}
