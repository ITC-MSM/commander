package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Bronze Sword
 * {1}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0.
 * Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The plain Equipment shape, identical to Torch Gauntlet: one [ModifyStats] static scoped to
 * [Filters.EquippedCreature] — the type's own default, the attached-to group — plus `equipAbility`,
 * which sets `equipCost` *and* lowers the sorcery-speed, equip-flagged attach ability in one call.
 * Never hand-roll that activated ability: an unflagged equip ability is invisible to every engine
 * rule keyed off `ActivatedAbility.isEquipAbility` (equip discounts, instant-speed-equip grants).
 */
val BronzeSword = card("Bronze Sword") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(2, 0, Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Kev Walker"
        flavorText = "Every strike of the smith's hammer infuses a sword with the determination of its creator."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b33162d-8d8f-4312-b31e-04ce86e3b914.jpg"
    }
}
