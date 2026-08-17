package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Melek, Reforged Researcher — Murders at Karlov Manor #430
 * {3}{U}{R} · Legendary Creature — Weird Detective · * / *
 *
 * Both characteristic-defining values share one dynamic amount: twice the number of instant and
 * sorcery cards in its controller's graveyard. As a characteristic-defining ability this is live
 * in every zone, including while Melek itself is in the graveyard.
 *
 * The cost modifier uses the first-matching-spell gate. Instant and sorcery are one combined class,
 * so casting either as the first matching spell consumes the reduction for both for that turn.
 */
val MelekReforgedResearcher = card("Melek, Reforged Researcher") {
    manaCost = "{3}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Weird Detective"
    oracleText = "Melek's power and toughness are each equal to twice the number of instant and " +
        "sorcery cards in your graveyard.\n" +
        "The first instant or sorcery spell you cast each turn costs {3} less to cast."

    dynamicStats(
        DynamicAmount.Multiply(
            DynamicAmount.Count(Player.You, Zone.GRAVEYARD, Filters.Unified.instantOrSorcery),
            2,
        ),
    )

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(Filters.Unified.instantOrSorcery),
            modification = CostModification.ReduceGeneric(3),
            gating = CostGating.NthOfTypePerTurn(1),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "430"
        artist = "Andreas Zafiratos"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/" +
            "01c5ede0-a098-4f21-8b7e-795a83e75aae.jpg?1783912764"
    }
}
