package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Witherbloom, the Balancer — Secrets of Strixhaven #245
 * {6}{B}{G} · Legendary Creature — Elder Dragon · 5/5
 *
 * Affinity for creatures (This spell costs {1} less to cast for each creature you control.)
 * Flying, deathtouch
 * Instant and sorcery spells you cast have affinity for creatures.
 *
 * Per CR 702.41a, "affinity for creatures" means "this spell costs {1} less to cast for each
 * creature you control". Both affinity clauses are [ModifySpellCost] static abilities reducing
 * the generic cost by the creature count ([CostReductionSource.PermanentsYouControlMatching]):
 *  - The keyword on Witherbloom itself → [SpellCostTarget.SelfCast].
 *  - "Instant and sorcery spells you cast have affinity for creatures" → granting affinity to a
 *    class of spells is mechanically identical to a battlefield-sourced
 *    [SpellCostTarget.YouCast] reduction (same pattern as Sami, Wildcat Captain), here filtered
 *    to [GameObjectFilter.InstantOrSorcery]. No dedicated "granted affinity" engine path needed.
 */
val WitherbloomTheBalancer = card("Witherbloom, the Balancer") {
    manaCost = "{6}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 5
    toughness = 5
    oracleText = "Affinity for creatures (This spell costs {1} less to cast for each creature you " +
        "control.)\n" +
        "Flying, deathtouch\n" +
        "Instant and sorcery spells you cast have affinity for creatures."

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    // Affinity for creatures — this spell costs {1} less to cast for each creature you control.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsYouControlMatching(GameObjectFilter.Creature),
            ),
        )
    }

    // Instant and sorcery spells you cast have affinity for creatures.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.InstantOrSorcery),
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsYouControlMatching(GameObjectFilter.Creature),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "245"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed7b2361-97c6-49e2-bf0b-4770f4ffe2f0.jpg?1775938710"
    }
}
