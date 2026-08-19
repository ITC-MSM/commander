package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedTriggeredAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Case of the Ransacked Lab — Murders at Karlov Manor #45
 * {2}{U} · Enchantment — Case · Rare
 *
 * Instant and sorcery spells you cast cost {1} less to cast.
 * To solve — You've cast four or more instant and sorcery spells this turn.
 * Solved — Whenever you cast an instant or sorcery spell, draw a card.
 *
 * The one Case whose *first* line is a static ability rather than an enters trigger, and the two
 * halves are deliberately the same spell type: the discount is what funds the four casts, and the
 * discount keeps working after the Case is solved — solving a Case never turns its other abilities
 * off.
 *
 * `ReduceGeneric(1)` rather than a blanket reduction, per the standard cost-reduction rulings: the
 * discount only shaves generic mana, so a `{U}{U}` spell is unaffected. The "to solve" count is
 * `YouCastSpellsThisTurn`, which reads the cast history — a spell that was countered, fizzled, or
 * is still on the stack all count, exactly as "you've cast" means.
 */
val CaseOfTheRansackedLab = card("Case of the Ransacked Lab") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Case"
    oracleText = "Instant and sorcery spells you cast cost {1} less to cast.\n" +
        "To solve — You've cast four or more instant and sorcery spells this turn. (If unsolved, " +
        "solve at the beginning of your end step.)\n" +
        "Solved — Whenever you cast an instant or sorcery spell, draw a card."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.InstantOrSorcery),
            modification = CostModification.ReduceGeneric(1)
        )
    }

    toSolve(Conditions.YouCastSpellsThisTurn(4, GameObjectFilter.InstantOrSorcery))

    solvedTriggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.DrawCards(1)
        description = "Solved — Whenever you cast an instant or sorcery spell, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16a9a596-61de-4fcf-aae0-41836c3deca5.jpg?1783912914"
    }
}
