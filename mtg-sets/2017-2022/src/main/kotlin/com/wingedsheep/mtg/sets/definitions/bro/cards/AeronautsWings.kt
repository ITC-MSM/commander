package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Aeronaut's Wings
 * {2}
 * Artifact — Equipment
 * Equipped creature gets +1/+0 and has flying.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The plain Equipment shape: two static abilities scoped to [Filters.EquippedCreature] plus
 * `equipAbility`, which sets `equipCost` and lowers the sorcery-speed equip activated ability in
 * one place.
 */
val AeronautsWings = card("Aeronaut's Wings") {
    manaCost = "{2}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 and has flying.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Leon Tukker"
        flavorText = "Volunteers rarely questioned why the airborne battalion always seemed to be recruiting."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcc65821-e4e7-471c-941c-9d3e25ae8bb9.jpg?1783920019"
    }
}
