package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * The Scarlet Witch
 * {2}{R}
 * Legendary Creature — Mutant Warlock Hero
 * 2/3
 *
 * Instant and sorcery spells you cast with mana value 4 or greater cost {X} less to cast,
 * where X is The Scarlet Witch's power.
 *
 * A battlefield-sourced [SpellCostTarget.YouCast] modifier whose amount reads the permanent it is
 * printed on, via [CostReductionSource.Dynamic] over [DynamicAmounts.sourcePower] — the same
 * `DynamicAmount` vocabulary the activated-ability cost rail uses. Her power is read from projected
 * state, so +1/+1 counters and anthems raise the discount, and a second copy discounts by *its own*
 * power rather than the biggest one on the battlefield.
 *
 * "Mana value 4 or greater" is checked against the spell's **mana cost** (CR 202.3), which a cost
 * reduction never touches — CR 601.2f subtracts reductions from the *total cost*, not from the mana
 * cost — so the discount can't disqualify the spell it applies to. As with every generic reduction
 * it cannot eat colored pips, and the mana component floors at {0} (also CR 601.2f), so a {3}{R}
 * spell bottoms out at {R}.
 *
 * **Known engine limitation — {X} spells.** CR 202.3e makes X the announced value while the spell is
 * on the stack, and CR 601.2b announces X before CR 601.2f determines the total cost, so a Fireball
 * cast for X = 5 has mana value 6 and should qualify. `CostCalculator` evaluates
 * `CardPredicate.ManaValueAtLeast` against the *printed* mana cost (X = 0), so it does not — the
 * Witch never discounts an {X} spell whose non-X portion is below mana value 4. Pre-existing and
 * shared with every other `ManaValueAtLeast` cost filter (Krosan Drover); fixing it means threading
 * the declared X into cost-time predicate evaluation, which is its own unit of work.
 */
val TheScarletWitch = card("The Scarlet Witch") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Mutant Warlock Hero"
    power = 2
    toughness = 3
    oracleText = "Instant and sorcery spells you cast with mana value 4 or greater cost {X} less " +
        "to cast, where X is The Scarlet Witch's power."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(
                GameObjectFilter.InstantOrSorcery.manaValueAtLeast(4)
            ),
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.Dynamic(DynamicAmounts.sourcePower())
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Magali Villeneuve"
        flavorText = "\"Some are bound by the whims of chaos, but I found freedom in its " +
            "endless possibility.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/407e8993-e56d-477d-ab85-d10a2522eab3.jpg?1783902924"
    }
}
