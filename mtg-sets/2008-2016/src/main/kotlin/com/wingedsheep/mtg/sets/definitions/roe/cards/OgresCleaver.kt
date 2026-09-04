package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Ogre's Cleaver
 * {2}
 * Artifact — Equipment
 *
 * Equipped creature gets +5/+0.
 * Equip {5}
 *
 * Modeling notes:
 *  - The plainest Equipment shape in the corpus: one `staticAbility { ModifyStats(...) }` scoped
 *    to `Filters.EquippedCreature` (`GroupFilter.attachedCreature()`), plus `equipAbility("{5}")`.
 *  - `equipAbility(...)` is preferred over a hand-rolled `activatedAbility { }`: it sets
 *    `equipCost` *and* builds the `ActivatedAbility.equip` lowering with `isEquipAbility = true`,
 *    the flag every engine rule that keys off equip (free-equip grants, equip discounts,
 *    instant-speed-equip permissions) reads. Its defaults already carry the printed reminder
 *    text's meaning — sorcery timing and `TargetFilter.CreatureYouControl`.
 *  - "+5/+0" is power only, hence `toughnessBonus = 0`.
 */
val OgresCleaver = card("Ogre's Cleaver") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +5/+0.\n" +
            "Equip {5}"

    staticAbility {
        ability = ModifyStats(5, 0, Filters.EquippedCreature)
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Adi Granov"
        flavorText = "She adopted the weapon of the slave-lord Kazuul, and with it, all his cruelty."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec319380-bede-49e8-9d0c-473688033601.jpg?1783941955"
    }
}
