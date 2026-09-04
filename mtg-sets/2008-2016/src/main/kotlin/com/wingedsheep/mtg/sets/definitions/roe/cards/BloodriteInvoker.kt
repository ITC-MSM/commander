package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Bloodrite Invoker
 * {2}{B}
 * Creature — Vampire Shaman
 * 3 / 1
 *
 * {8}: Target player loses 3 life and you gain 3 life.
 *
 * Modeling notes:
 *  - The level-up-less "invoker" cycle is a plain [activatedAbility] with a `{8}` mana cost and no
 *    tap symbol, so it can be activated repeatedly and at instant speed — [Costs.Mana] alone, no
 *    [Costs.Tap] and no timing restriction.
 *  - Two life clauses sharing one target slot, exactly as Absorb Vis: [Effects.LoseLife] aimed at
 *    the bound player, [Effects.GainLife] left on its default controller recipient. Only the loss
 *    is targeted — the gain says "you" — so a single [TargetPlayer] requirement is declared.
 *  - It is **target player**, not target opponent: the printed word allows you to point it at
 *    yourself, so [TargetPlayer] rather than `TargetOpponent`.
 *  - The two amounts are independent fixed 3s, not a linked drain: you gain 3 even if the target
 *    had less than 3 life to lose.
 */
val BloodriteInvoker = card("Bloodrite Invoker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shaman"
    power = 3
    toughness = 1
    oracleText = "{8}: Target player loses 3 life and you gain 3 life."

    activatedAbility {
        cost = Costs.Mana("{8}")
        val victim = target("target player", TargetPlayer())
        effect = Effects.Composite(
            Effects.LoseLife(3, victim),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Svetlin Velinov"
        flavorText = "\"The brood lineages unfolded across the world, each patterned after one of three progenitors, each a study in mindless consumption.\"\n—*The Invokers' Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5dc36f94-2499-4d94-a506-27d42b9da667.jpg?1783941988"
    }
}
