package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Temur Battlecrier — Tarkir: Dragonstorm #228
 * {G}{U}{R} · Creature — Orc Ranger · 4/3
 *
 * During your turn, spells you cast cost {1} less to cast for each creature you
 * control with power 4 or greater.
 *
 * Modeled as a battlefield-sourced [ModifySpellCost] over every spell its controller casts
 * ([SpellCostTarget.YouCast] with no card filter). The per-unit reduction counts creatures
 * the controller has with power 4+ via [CostReductionSource.PermanentsYouControlMatching]
 * (projected power, so counters and lords count — and the Battlecrier itself, a 4-power
 * creature, contributes once it resolves). The "During your turn" clause is the
 * [CostGating.OnlyIf] gate on [Conditions.IsYourTurn], so the discount vanishes on opponents'
 * turns.
 */
val TemurBattlecrier = card("Temur Battlecrier") {
    manaCost = "{G}{U}{R}"
    colorIdentity = "GUR"
    typeLine = "Creature — Orc Ranger"
    power = 4
    toughness = 3
    oracleText = "During your turn, spells you cast cost {1} less to cast for each creature " +
        "you control with power 4 or greater."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsYouControlMatching(
                    GameObjectFilter.Creature.powerAtLeast(4),
                ),
            ),
            gating = CostGating.OnlyIf(Conditions.IsYourTurn),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Brent Hollowell"
        flavorText = "When called to the land's defense, all come to protect their shared home. " +
            "It's why many of the Temur use \"family\" and \"herd\" interchangeably."
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72184791-0767-4108-920c-763e92dae2d4.jpg?1743204904"
    }
}
