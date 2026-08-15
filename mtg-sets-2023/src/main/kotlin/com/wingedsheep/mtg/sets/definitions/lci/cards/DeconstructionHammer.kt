package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deconstruction Hammer (LCI #9) — {W} Artifact — Equipment (common)
 *
 * Equipped creature gets +1/+1 and has "{3}, {T}, Sacrifice Deconstruction Hammer:
 * Destroy target artifact or enchantment."
 * Equip {1}
 *
 * Implementation notes:
 * - The +1/+1 buff is a [ModifyStats] static scoped to [Filters.EquippedCreature].
 * - The quoted activated ability is granted to the equipped creature via
 *   [GrantActivatedAbility] (the Swashbuckler's Whip idiom): it lives on the creature, its
 *   `{T}` taps that creature (the granted ability's source, CR 113.7), and the mana +
 *   sacrifice are paid by that creature's controller. [Effects.Destroy] hits the ability's
 *   sole target ([Targets.ArtifactOrEnchantment]).
 * - "Sacrifice Deconstruction Hammer" refers only to the specific granting Equipment
 *   (CR 201.5a), so the cost is [Costs.SacrificeGrantingPermanent], which sacrifices exactly
 *   the granter resolved at activation time — no name filter, no target prompt, and correct
 *   with a second Deconstruction Hammer on the battlefield.
 */
val DeconstructionHammer = card("Deconstruction Hammer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and has \"{3}, {T}, Sacrifice Deconstruction Hammer: " +
        "Destroy target artifact or enchantment.\"\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    // Equipped creature gets +1/+1.
    staticAbility {
        ability = ModifyStats(+1, +1, Filters.EquippedCreature)
    }

    // ... and has "{3}, {T}, Sacrifice Deconstruction Hammer: Destroy target artifact or enchantment."
    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(
                    Costs.Mana("{3}"),
                    Costs.Tap,
                    Costs.SacrificeGrantingPermanent
                ),
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(Targets.ArtifactOrEnchantment),
                descriptionOverride = "{3}, {T}, Sacrifice Deconstruction Hammer: Destroy target artifact or enchantment."
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Dibujante Nocturno"
        flavorText = "\"All that is made can be unmade. Chimil's light is the only constant.\"\n—Oltec artificers' creed"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c7ba382-18c4-4833-b3d2-bd469ae2ad77.jpg?1782694606"
    }
}
