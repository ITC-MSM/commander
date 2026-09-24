package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AssignDamageEqualToToughness
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty

/**
 * Ghalta the Immovable (Reality Fracture #197) — {8}{W} Legendary Creature — Elder Dinosaur 0/7.
 *
 * Ghalta the Unstoppable's cost reduction read off toughness instead of power (generic part only,
 * so it never costs less than {W}), Ancient Lumberknot's toughness-damage static, and the
 * battlefield-scoped form of [CanAttackDespiteDefender]: no condition, and a group filter over the
 * creatures Ghalta's controller controls — Ghalta itself included.
 */
val GhaltaTheImmovable = card("Ghalta the Immovable") {
    manaCost = "{8}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Elder Dinosaur"
    oracleText = "This spell costs {X} less to cast, where X is the greatest toughness among creatures you control.\n" +
        "Creatures you control can attack as though they didn't have defender.\n" +
        "Each creature you control with toughness greater than its power assigns combat damage equal to its " +
        "toughness rather than its power."
    power = 0
    toughness = 7

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.GreatestPropertyAmongPermanentsYouControl(
                    EntityNumericProperty.Toughness, Filters.Creature
                )
            ),
        )
    }

    staticAbility {
        ability = CanAttackDespiteDefender(filter = GroupFilter.AllCreaturesYouControl)
    }

    staticAbility {
        ability = AssignDamageEqualToToughness(
            filter = GroupFilter.AllCreaturesYouControl,
            onlyWhenToughnessGreaterThanPower = true,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9f3aa55-908f-42db-8135-4201433df850.jpg?1789014423"
        inBooster = false
    }
}
