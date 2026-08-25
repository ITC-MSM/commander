package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Supreme Phantom
 * {1}{U}
 * Creature — Spirit
 * 1/3
 * Flying
 * Other Spirits you control get +1/+1.
 *
 * "Spirits" is a bare tribal noun, so it names the subtype rather than the card type — a
 * noncreature Spirit permanent you control is one of them. `excludeSelf` is the printed "Other".
 */
val SupremePhantom = card("Supreme Phantom") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "Other Spirits you control get +1/+1."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "76"
        artist = "Robbie Trevino"
        flavorText = "A king's knowledge does not vanish when the heart stops beating."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d65c22b-7640-4433-930b-4bc381ac7361.jpg"
    }
}
