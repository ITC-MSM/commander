package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessDynamicStatic
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Titania's Song
 * {3}{G}
 * Enchantment
 * Each noncreature artifact loses all abilities and becomes an artifact creature with power and
 * toughness each equal to its mana value. If this enchantment leaves the battlefield, this effect
 * continues until end of turn.
 *
 * Modeled as three continuous group statics over noncreature artifacts (CR 613):
 *  - Layer 4 (TYPE): GrantCardType("CREATURE") — makes them creatures (already artifacts).
 *  - Layer 6 (ABILITY): LoseAllAbilities.
 *  - Layer 7b (POWER_TOUGHNESS, SET_VALUES): SetBasePowerToughnessDynamicStatic with each
 *    permanent's own mana value (EntityReference.AffectedEntity → ManaValue).
 *
 * The "noncreature artifact" filter (`Artifact.notCreature()`) is locked in at effect-collection
 * time — it is not a [com.wingedsheep.sdk.scripting.predicates.CardPredicate.IsCreature]-keyed
 * filter, so the projector does not re-resolve it after Layer 4 turns the artifacts into
 * creatures; the same set is animated, stripped, and re-sized in Layers 4/6/7b (the
 * Opalescence/Conspiracy locked-set rule).
 *
 * The "continues until end of turn" linger is a leaves-the-battlefield self-trigger that replays
 * the same animate-with-ability-removal as one-shot floating effects (Effects.MassAnimate, P/T = mana value)
 * over whatever noncreature artifacts exist when the enchantment leaves.
 */
val TitaniasSong = card("Titania's Song") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Each noncreature artifact loses all abilities and becomes an artifact creature " +
        "with power and toughness each equal to its mana value. If this enchantment leaves the " +
        "battlefield, this effect continues until end of turn."

    val noncreatureArtifacts = GroupFilter(GameObjectFilter.Artifact.notCreature())
    val manaValue: DynamicAmount = DynamicAmount.EntityProperty(
        entity = EntityReference.AffectedEntity,
        numericProperty = EntityNumericProperty.ManaValue
    )

    staticAbility { ability = GrantCardType(cardType = "CREATURE", filter = noncreatureArtifacts) }
    staticAbility { ability = LoseAllAbilities(filter = noncreatureArtifacts) }
    staticAbility {
        ability = SetBasePowerToughnessDynamicStatic(
            power = manaValue,
            toughness = manaValue,
            filter = noncreatureArtifacts
        )
    }

    // "If this enchantment leaves the battlefield, this effect continues until end of turn."
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(binding = TriggerBinding.SELF)
        effect = Effects.MassAnimate(
            filter = GameObjectFilter.Artifact.notCreature(),
            power = manaValue,
            toughness = manaValue,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Kerstin Kaman"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/583a53af-2e2a-4f3f-8eab-bd874c6ed80a.jpg?1562913410"
    }
}
