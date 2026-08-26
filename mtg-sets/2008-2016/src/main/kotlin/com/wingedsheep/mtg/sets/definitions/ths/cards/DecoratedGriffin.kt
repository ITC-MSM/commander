package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.effects.PreventionScope
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Decorated Griffin
 * {4}{W}
 * Creature — Griffin
 * 2 / 3
 *
 * Flying
 * {1}{W}: Prevent the next 1 combat damage that would be dealt to you this turn.
 *
 * The shield's recipient is the controller — [PreventDamageEffect]'s default `target` — so "dealt
 * to you" is the absence of a target, not a target of its own. `Effects` publishes fourteen
 * prevention facades and none of them freezes *this* point of the product (a bounded amount **and**
 * [PreventionScope.CombatOnly]), so the type is constructed directly, as Al-abara's Carpet does for
 * the same reason. Keeping the amount non-null is load-bearing: an amount-less combat-only shield
 * on the default target is what `PreventDamageExecutor` reads as the global Fog.
 */
val DecoratedGriffin = card("Decorated Griffin") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 3
    oracleText = "Flying\n{1}{W}: Prevent the next 1 combat damage that would be dealt to you this turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = PreventDamageEffect(
            amount = DynamicAmount.Fixed(1),
            scope = PreventionScope.CombatOnly
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Phill Simmer"
        flavorText = "The awards and medals of polis-dwellers mean nothing to griffins, but they repay acts of generosity."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bc2f3a0-444d-4cad-970f-6c4a63b02cce.jpg"
    }
}
