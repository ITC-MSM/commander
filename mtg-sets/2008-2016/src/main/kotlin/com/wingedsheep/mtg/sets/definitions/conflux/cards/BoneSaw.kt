package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Bone Saw
 * {0}
 * Artifact — Equipment
 * Equipped creature gets +1/+0.
 * Equip {1}
 *
 * The plainest Equipment shape there is: a [ModifyStats] static for the printed buff, and
 * [equipAbility] for the printed "Equip {1}" line. The equip half is deliberately *not*
 * hand-rolled as `activatedAbility { isEquipAbility = true }` — an unflagged equip ability is
 * invisible to every engine rule keyed off `ActivatedAbility.isEquipAbility` (Leonin Shikari's
 * instant-speed permission, Forge Anew's free first equip). The facade also supplies the sorcery
 * timing and the "creature you control" target requirement that Assay's model shows.
 */
val BoneSaw = card("Bone Saw") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Pete Venters"
        flavorText = "In a world where death is always violent, cruel weapons are as common as rocks."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3bf79d6-4b4a-4fdd-a831-36eff2523661.jpg"
    }
}
