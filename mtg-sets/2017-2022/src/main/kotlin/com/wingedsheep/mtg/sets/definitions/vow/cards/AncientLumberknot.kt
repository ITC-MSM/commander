package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AssignDamageEqualToToughness
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ancient Lumberknot
 * {2}{B}{G}
 * Creature — Treefolk
 * 1/4
 *
 * Each creature you control with toughness greater than its power assigns combat damage equal to
 * its toughness rather than its power.
 *
 * One static, and the SDK already spells this exact sentence:
 * [AssignDamageEqualToToughness]`(AllCreaturesYouControl, onlyWhenToughnessGreaterThanPower = true)`
 * — the same ability Bedrock Tortoise and Tapestry Warden carry. The `onlyWhen…` flag is what
 * separates this family's two halves: Doran and Assault Formation drop the qualifier and swap the
 * assignment for *every* creature in the group, while this card's printed "with toughness greater
 * than its power" restricts it to the creatures the swap would actually help. The Lumberknot's own
 * 1/4 body qualifies, so it attacks as a 4-power creature while staying a 1/4 for every other
 * purpose (CR 510.1a assigns damage; it does not change power).
 */
val AncientLumberknot = card("Ancient Lumberknot") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Treefolk"
    power = 1
    toughness = 4
    oracleText = "Each creature you control with toughness greater than its power assigns combat " +
        "damage equal to its toughness rather than its power."

    staticAbility {
        ability = AssignDamageEqualToToughness(
            filter = GroupFilter.AllCreaturesYouControl,
            onlyWhenToughnessGreaterThanPower = true,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "230"
        artist = "Nicholas Gregory"
        flavorText = "The soft creak of trees in the wind is not always cause for comfort."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22264087-bac4-4746-b6ce-0d44cce163e6.jpg?1783924797"
    }
}
