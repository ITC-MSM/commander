package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blazing Torch
 * {1}
 * Artifact — Equipment — Uncommon (ZEN #197)
 *
 * "Equipped creature can't be blocked by Vampires or Zombies.
 *  Equipped creature has "{T}, Sacrifice Blazing Torch: Blazing Torch deals 2 damage to any target."
 *  Equip {1}"
 *
 * Implementation notes:
 *  - The evasion clause is [CantBeBlockedBy] over "creature that is a Vampire or a Zombie"
 *    ([GameObjectFilter.withAnySubtype]), scoped to [Filters.EquippedCreature].
 *  - The quoted ability is granted to the bearer via [GrantActivatedAbility] (the Deconstruction
 *    Hammer idiom): the `{T}` taps *that creature* (CR 113.7 — the granted ability's source is the
 *    creature, so it is also subject to summoning sickness), and its controller pays the cost.
 *  - "Sacrifice Blazing Torch" names the specific granting Equipment (CR 201.5a), so the cost is
 *    [Costs.SacrificeGrantingPermanent] — correct even with a second Blazing Torch in play, and it
 *    never offers the creature's own controller a choice of which artifact to sacrifice.
 *  - "**Blazing Torch** deals 2 damage" — the damage source is the Equipment, not the creature, so
 *    `damageSource = EffectTarget.GrantingSource`. This matters: the Torch has been sacrificed by
 *    the time the ability resolves (last-known information), it is a colorless artifact source, and
 *    a lifelinking / deathtouching bearer must not lend those abilities to the damage.
 */
val BlazingTorch = card("Blazing Torch") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature can't be blocked by Vampires or Zombies.\n" +
        "Equipped creature has \"{T}, Sacrifice Blazing Torch: Blazing Torch deals 2 damage to any target.\"\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    // Equipped creature can't be blocked by Vampires or Zombies.
    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.Creature.withAnySubtype("Vampire", "Zombie"),
            filter = Filters.EquippedCreature
        )
    }

    // Equipped creature has "{T}, Sacrifice Blazing Torch: Blazing Torch deals 2 damage to any target."
    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(
                    Costs.Tap,
                    Costs.SacrificeGrantingPermanent
                ),
                effect = DealDamageEffect(
                    amount = 2,
                    target = EffectTarget.ContextTarget(0),
                    damageSource = EffectTarget.GrantingSource
                ),
                targetRequirements = listOf(Targets.Any),
                descriptionOverride = "{T}, Sacrifice Blazing Torch: Blazing Torch deals 2 damage to any target."
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Vance Kovacs"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e9d1ff2-9ce3-4737-af1d-9fc82e4dffe6.jpg?1783942128"
    }
}
