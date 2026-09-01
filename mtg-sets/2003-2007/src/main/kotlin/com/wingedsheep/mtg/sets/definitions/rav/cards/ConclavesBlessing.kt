package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Conclave's Blessing
 * {3}{W}
 * Enchantment — Aura
 *
 * Convoke
 * Enchant creature
 * Enchanted creature gets +0/+2 for each other creature you control.
 *
 * "Other" is measured against the *enchanted* creature, not against the Aura — and that is what
 * `excludeSelf` means here: [DynamicAmount.AggregateBattlefield] takes "self" to be the affected
 * entity when the aggregate is evaluated for a granted effect, which for an Aura's stat grant is
 * the creature it enchants. "You" stays the Aura's controller, so enchanting an opponent's
 * creature counts *your* creatures and excludes nothing (the host isn't among them).
 */
val ConclavesBlessing = card("Conclave's Blessing") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Enchant creature\n" +
        "Enchanted creature gets +0/+2 for each other creature you control."

    keywords(Keyword.CONVOKE)

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = Filters.EnchantedCreature,
            powerBonus = DynamicAmount.Fixed(0),
            // +2 per creature, not +1 — the printed bonus is +0/+2 *for each*, so the count has
            // to be doubled before it becomes the toughness bonus.
            toughnessBonus = DynamicAmount.Multiply(
                DynamicAmount.AggregateBattlefield(
                    player = Player.You,
                    filter = GameObjectFilter.Creature,
                    excludeSelf = true
                ),
                multiplier = 2
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Shishizaru"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86235d9f-ba69-417d-9203-812ab428b374.jpg?1783943702"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
        ruling(
            "2024-01-12",
            "Tapping a multicolored creature using convoke will pay for {1} or one mana of your " +
                "choice of any of that creature's colors."
        )
    }
}
