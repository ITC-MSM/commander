package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sunrise Sovereign
 * {5}{R}
 * Creature — Giant Warrior
 * 5/5
 * Other Giant creatures you control get +2/+2 and have trample.
 *
 * One printed sentence, two static abilities: the pump applies in Layer 7c and the keyword grant in
 * Layer 6, so they are separate `StaticAbility` values over the same group rather than one compound
 * type. `excludeSelf` is what "Other" says, and it is a property of the iteration rather than of the
 * noun phrase.
 */
val SunriseSovereign = card("Sunrise Sovereign") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 5
    toughness = 5
    oracleText = "Other Giant creatures you control get +2/+2 and have trample."

    val otherGiants = GroupFilter(
        GameObjectFilter.Creature.withSubtype(Subtype.GIANT).youControl(),
        excludeSelf = true,
    )

    staticAbility {
        ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = otherGiants)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, otherGiants)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "William O'Connor"
        flavorText = "A hundred generations has he mentored, a hundred armies has he crushed beneath his feet, yet only a hundred words has he ever spoken, each more revered than a hundred books."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b62a4703-9e8d-4164-a529-b2dbc4069aa8.jpg?1783942869"
    }
}
