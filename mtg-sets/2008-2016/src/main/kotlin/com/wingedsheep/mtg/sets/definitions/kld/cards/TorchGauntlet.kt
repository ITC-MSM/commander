package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Torch Gauntlet
 * {2}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The plain Equipment shape: one [ModifyStats] static scoped to [Filters.EquippedCreature], plus
 * `equipAbility`, which sets `equipCost` and lowers the sorcery-speed, equip-flagged attach ability
 * in one place — never a hand-rolled activated ability, because "equip costs less" and
 * "equip at instant speed" effects key off that flag.
 */
val TorchGauntlet = card("Torch Gauntlet") {
    manaCost = "{2}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(2, 0, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Igor Kieryluk"
        flavorText = "\"Good morning, children. Please open your aether valves and ignite your gauntlets.\"\n—Rakib, Ghirapur schoolteacher"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfcf9a88-c5f8-4c26-a2ee-d2827e9a31d8.jpg?1783937148"
    }
}
