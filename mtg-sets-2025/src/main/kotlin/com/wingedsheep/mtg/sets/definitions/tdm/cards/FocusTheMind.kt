package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Focus the Mind — Tarkir: Dragonstorm #45
 * {4}{U} · Instant
 *
 * This spell costs {2} less to cast if you've cast another spell this turn.
 * Draw three cards, then discard a card.
 *
 * The cost reduction is evaluated at cast time, before this spell is counted, so
 * `YouCastSpellsThisTurn(atLeast = 1)` means "you've cast another spell this turn".
 */
val FocusTheMind = card("Focus the Mind") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if you've cast another spell this turn.\nDraw three cards, then discard a card."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.YouCastSpellsThisTurn(atLeast = 1)),
        )
    }

    spell {
        effect = Effects.DrawCards(3) then Effects.Discard(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Fariba Khamseh"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abb0ba34-6904-4c17-a04d-ea4f12c7cf21.jpg?1743204141"
    }
}
