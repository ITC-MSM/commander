package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Metamorphosis
 * {G}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a creature.
 * Add X mana of any one color, where X is 1 plus the sacrificed creature's mana value.
 * Spend this mana only to cast creature spells.
 *
 * The sacrificed creature's mana value is read from the cost-payment snapshot
 * (last-known information), since the creature is in the graveyard by resolution
 * time — see [EntityReference.Sacrificed]. The produced mana is a single chosen
 * color, added `1 + the sacrificed creature's mana value` times, carrying
 * [ManaRestriction.CreatureSpellsOnly] so it can only pay for creature spells
 * (including their additional costs such as Kicker, per the 2004 ruling).
 */
val Metamorphosis = card("Metamorphosis") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\n" +
        "Add X mana of any one color, where X is 1 plus the sacrificed creature's mana value. " +
        "Spend this mana only to cast creature spells."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        effect = Effects.AddAnyColorMana(
            amount = DynamicAmount.Add(
                DynamicAmount.Fixed(1),
                DynamicAmount.EntityProperty(
                    EntityReference.Sacrificed(0),
                    EntityNumericProperty.ManaValue
                )
            ),
            restriction = ManaRestriction.CreatureSpellsOnly
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbc6cfc3-b232-40bf-bc0c-4618f6f5c9a5.jpg?1562942451"
        ruling("2013-04-15", "You must sacrifice exactly one creature to cast this spell; you cannot cast it without sacrificing a creature, and you cannot sacrifice additional creatures.")
        ruling("2004-10-04", "This mana may be used on additional costs to cast the spell, such as Kicker.")
    }
}
