package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Alpha Status
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 for each other creature on the battlefield
 * that shares a creature type with it.
 */
val AlphaStatus = card("Alpha Status") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +2/+2 for each other creature on the battlefield that shares a creature type with it."

    auraTarget = Targets.Creature

    staticAbility {
        // "each OTHER creature that shares a creature type with the enchanted creature" — count
        // every battlefield creature sharing a type with the affected (enchanted) creature, then
        // exclude that creature itself. excludeSelf resolves "self" to the affected entity here,
        // since the bonus is granted to the enchanted creature rather than to the Aura source.
        val sharedTypeCount = DynamicAmount.AggregateBattlefield(
            player = Player.Each,
            filter = GameObjectFilter.Creature.sharingCreatureTypeWith(EntityReference.AffectedEntity),
            excludeSelf = true,
        )
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = DynamicAmount.Multiply(sharedTypeCount, 2),
            toughnessBonus = DynamicAmount.Multiply(sharedTypeCount, 2)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "Darrell Riche"
        flavorText = "\"The best leaders are made by their followers.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd210c45-57f3-4d7d-93ba-81fe4298ade3.jpg?1562537375"

        ruling("2004-10-04", "Alpha Status counts each creature once if that creature shares at least one creature type with the enchanted creature.")
    }
}
