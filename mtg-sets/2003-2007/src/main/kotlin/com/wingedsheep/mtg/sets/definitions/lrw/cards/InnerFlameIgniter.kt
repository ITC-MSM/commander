package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Inner-Flame Igniter — Lorwyn #182
 * {2}{R} · Creature — Elemental Warrior · 2/2
 *
 * {2}{R}: Creatures you control get +1/+0 until end of turn. If this is the third time this
 * ability has resolved this turn, creatures you control gain first strike until end of turn.
 *
 * The Harvestrite Host shape: [IncrementAbilityResolutionCountEffect] bumps the source's
 * per-turn resolution tally, then [Conditions.SourceAbilityResolvedNTimes] reads it back. The
 * increment must sit *between* the two clauses — the condition compares against the count
 * including this resolution, so a bump after the check would fire the bonus on the fourth
 * activation instead of the third. The ruling that only the *third* resolution pays out is
 * therefore free: the condition is an equality, not a threshold.
 *
 * Two separate group passes, not [Patterns.Group.pumpAndGrantToAll]: the printed text names
 * "creatures you control" twice, in two clauses that fire independently — the pump every time,
 * the first strike only on the third. Overrun's single-pass form would bind the group once and
 * grant unconditionally.
 *
 * The tally counts *resolutions*, which is what the effect being composed into the ability's
 * own resolution gives us for free — an activation still on the stack, or one countered, never
 * reaches the increment.
 */
val InnerFlameIgniter = card("Inner-Flame Igniter") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Warrior"
    power = 2
    toughness = 2
    oracleText = "{2}{R}: Creatures you control get +1/+0 until end of turn. If this is the third " +
        "time this ability has resolved this turn, creatures you control gain first strike until " +
        "end of turn."

    val creaturesYouControl = GroupFilter(GameObjectFilter.Creature.youControl())

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Patterns.Group.modifyStatsForAll(1, 0, creaturesYouControl)
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(3),
                    effect = Patterns.Group.grantKeywordToAll(Keyword.FIRST_STRIKE, creaturesYouControl)
                )
            )
        description = "Creatures you control get +1/+0 until end of turn. If this is the third " +
            "time this ability has resolved this turn, creatures you control gain first strike " +
            "until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "182"
        artist = "Scott Hampton"
        flavorText = "A light an army can follow."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07ced8a0-1caa-4737-b863-eb244a3d388a.jpg?1783942872"
        ruling("2007-10-01", "Counts resolutions, not activations. Any such abilities that are still on the stack won't count toward the total.")
        ruling("2007-10-01", "You get the bonus only the third time the ability resolves. You won't get the bonus the fourth, fifth, sixth, or any subsequent times.")
    }
}
