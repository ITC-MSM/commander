package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.IsAllCreatureTypes
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Runed Stalactite
 * {1}
 * Artifact — Equipment
 * Equipped creature gets +1/+1 and is every creature type.
 * Equip {2}
 *
 * "Is every creature type" is [IsAllCreatureTypes], a Layer 4 type-setting static — it adds the
 * types without granting changeling, which is the right distinction: the equipped creature becomes
 * every type but the *Equipment* is not itself a changeling permanent, and nothing that counts
 * changeling permanents should see it. Pointed at the attached creature it turns any body into a
 * lord magnet for every tribe in the set.
 */
val RunedStalactite = card("Runed Stalactite") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and is every creature type.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = IsAllCreatureTypes(GroupFilter.attachedCreature())
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "260"
        artist = "Jim Pavelec"
        flavorText = "When a changeling adopts a form no other changeling has taken, a rune " +
            "appears in the caverns of Velis Vel to mark the event."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9be88336-83c7-422d-8826-13ceb8db5534.jpg?1783942852"
    }
}
