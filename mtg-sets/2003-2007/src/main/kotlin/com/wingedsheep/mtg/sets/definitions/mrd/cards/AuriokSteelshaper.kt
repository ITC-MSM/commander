package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.ReduceEquipCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Auriok Steelshaper — Mirrodin #4
 * {1}{W} · Creature — Human Soldier · 1/1 · Rare
 *
 * Equip costs you pay cost {1} less.
 * As long as this creature is equipped, each creature you control that's a Soldier or a Knight
 * gets +1/+1.
 *
 * Modelling notes:
 * - The discount is [ReduceEquipCost] (the Éowyn, Lady of Rohan primitive): controller-scoped,
 *   generic-only, floored at {0}, and it applies to every equip ability its controller activates
 *   — not only to Equipment attached to the Steelshaper. Two Steelshapers stack additively.
 * - The lord is gated by [Conditions.SourceMatches] with `.equipped()` — "as long as **this
 *   creature** is equipped", read off the Steelshaper's own attachments, not the group's. When
 *   the Equipment moves away the bonus goes with it.
 * - The buffed group is `withAnySubtype("Soldier", "Knight")` and includes the Steelshaper
 *   itself: it is a Soldier, and the card says "each creature you control", not "each other".
 */
val AuriokSteelshaper = card("Auriok Steelshaper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "Equip costs you pay cost {1} less.\n" +
        "As long as this creature is equipped, each creature you control that's a Soldier or a " +
        "Knight gets +1/+1."

    staticAbility {
        ability = ReduceEquipCost(amount = 1)
    }

    staticAbility {
        condition = Conditions.SourceMatches(GameObjectFilter.Any.equipped())
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl().withAnySubtype("Soldier", "Knight")
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Dany Orizio"
        flavorText = "They put their safety in his hands. He puts sharpened steel in theirs."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dd09c01-5a77-4992-96ae-c395a5966a92.jpg?1783944563"
    }
}
