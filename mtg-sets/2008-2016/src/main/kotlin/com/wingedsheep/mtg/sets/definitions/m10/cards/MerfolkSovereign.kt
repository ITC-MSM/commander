package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Merfolk Sovereign
 * {1}{U}{U}
 * Creature — Merfolk Noble
 * 2/2
 *
 * Other Merfolk creatures you control get +1/+1.
 * {T}: Target Merfolk creature can't be blocked this turn.
 *
 * The lord clause is the usual `GroupFilter(..., excludeSelf = true)` — "other" Merfolk you
 * control. The activated ability targets *any* Merfolk creature (no "you control" clause), and
 * grants [AbilityFlag.CANT_BE_BLOCKED] for the turn.
 */
val MerfolkSovereign = card("Merfolk Sovereign") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Noble"
    power = 2
    toughness = 2
    oracleText = "Other Merfolk creatures you control get +1/+1.\n{T}: Target Merfolk creature can't be blocked this turn."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.MERFOLK).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.MERFOLK)))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
        description = "Target Merfolk creature can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "62"
        artist = "Jesper Ejsing"
        flavorText = "\"The sharks envy our ferocity. The eels envy our cunning.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9826a2e-361e-4701-9177-0907c0c6dc9f.jpg?1783942390"

        ruling(
            "2009-10-01",
            "To have any effect, Merfolk Sovereign's activated ability must be activated before the " +
                "declare blockers step begins. Once a Merfolk has become blocked, activating Merfolk " +
                "Sovereign's ability won't change that."
        )
    }
}
