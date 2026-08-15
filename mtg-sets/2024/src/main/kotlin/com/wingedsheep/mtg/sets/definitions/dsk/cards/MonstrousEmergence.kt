package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Monstrous Emergence
 * {1}{G}
 * Sorcery
 * As an additional cost to cast this spell, choose a creature you control or reveal a creature
 * card from your hand.
 * Monstrous Emergence deals damage equal to the power of the creature you chose or the card you
 * revealed to target creature.
 *
 * Modeled like Close Encounter: an `AdditionalCost.ChooseEntity` over the caster's battlefield
 * creatures and hand creature cards, capturing a power snapshot (LKI) so the damage still resolves
 * if the chosen battlefield creature later leaves play. The damage reads the chosen entity's power
 * via `EntityReference.FromCostStorage`.
 *
 * "You control" / "from your hand" are implicit — per-zone iteration already restricts to the
 * caster's battlefield slice and the caster's hand.
 */
val MonstrousEmergence = card("Monstrous Emergence") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, choose a creature you control or reveal " +
        "a creature card from your hand.\n" +
        "Monstrous Emergence deals damage equal to the power of the creature you chose or the card " +
        "you revealed to target creature."

    additionalCost(
        Costs.additional.ChooseEntity(
            zoneFilters = mapOf(
                Zone.BATTLEFIELD to GameObjectFilter.Creature,
                Zone.HAND to GameObjectFilter.Creature,
            ),
            storeAs = "chosen",
            captureSnapshot = true,
            descriptionOverride = "choose a creature you control or reveal a creature card from your hand",
        )
    )

    spell {
        val damaged = target("creature", Targets.Creature)
        effect = Effects.DealDamage(
            DynamicAmount.EntityProperty(
                EntityReference.FromCostStorage("chosen"),
                EntityNumericProperty.Power,
            ),
            damaged,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b999eb47-b842-47f1-be91-c79fc46e1896.jpg?1726328518"
    }
}
