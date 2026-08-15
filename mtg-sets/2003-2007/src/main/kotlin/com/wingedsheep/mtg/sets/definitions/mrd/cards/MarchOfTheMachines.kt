package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessDynamicStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * March of the Machines — Mirrodin #42
 * {3}{U} · Enchantment
 *
 * Each noncreature artifact is an artifact creature with power and toughness each equal to its
 * mana value. (Equipment that's a creature can't equip a creature.)
 *
 * Titania's Song (ATQ) minus the ability wipe and minus the leaves-the-battlefield linger, so it
 * is two continuous group statics over noncreature artifacts (CR 613):
 *  - Layer 4 (TYPE): [GrantCardType] "CREATURE" — they are already artifacts, so this alone makes
 *    them artifact creatures, and any other card types (an artifact land, an artifact enchantment)
 *    are kept.
 *  - Layer 7b (POWER_TOUGHNESS, SET_VALUES): [SetBasePowerToughnessDynamicStatic] fed each
 *    permanent's own mana value ([EntityReference.AffectedEntity] → [EntityNumericProperty.ManaValue]).
 *
 * The `Artifact.notCreature()` filter is locked in at effect-collection time — it is not an
 * IsCreature-keyed filter the projector re-resolves after Layer 4 — so the same set is animated in
 * Layer 4 and re-sized in Layer 7b (the Opalescence/Conspiracy locked-set rule).
 *
 * Three printed consequences fall out of the layers rather than needing card-specific wiring:
 * artifact lands have mana value 0 and so die as 0/0s to state-based actions; a later animation
 * effect (Chimeric Staff) applies in the same layer with a later timestamp and wins; and an
 * Equipment that becomes a creature is unattached by the CR 301.5c state-based action, which is
 * exactly what the reminder text describes.
 */
val MarchOfTheMachines = card("March of the Machines") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Each noncreature artifact is an artifact creature with power and toughness each " +
        "equal to its mana value. (Equipment that's a creature can't equip a creature.)"

    val noncreatureArtifacts = GroupFilter(GameObjectFilter.Artifact.notCreature())
    val manaValue: DynamicAmount = DynamicAmount.EntityProperty(
        entity = EntityReference.AffectedEntity,
        numericProperty = EntityNumericProperty.ManaValue
    )

    staticAbility { ability = GrantCardType(cardType = "CREATURE", filter = noncreatureArtifacts) }
    staticAbility {
        ability = SetBasePowerToughnessDynamicStatic(
            power = manaValue,
            toughness = manaValue,
            filter = noncreatureArtifacts
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Ben Thompson"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9bdcc966-7837-463f-ae26-1096f34ac8c0.jpg?1783944553"
        ruling(
            "2004-12-01",
            "If an Equipment becomes a creature, it can no longer equip a creature. If it's " +
                "currently attached to a creature, it becomes unattached (but remains on the " +
                "battlefield). You can activate the Equipment's equip ability, but it won't do anything."
        )
        ruling(
            "2004-12-01",
            "Each artifact land has a mana value of 0. March of the Machines makes them 0/0 " +
                "creatures, which are put into the graveyard as a state-based action."
        )
        ruling(
            "2007-07-15",
            "If a noncreature artifact becomes an artifact creature this way and then another " +
                "effect animates it, the new effect overrides March of the Machines's effect."
        )
    }
}
