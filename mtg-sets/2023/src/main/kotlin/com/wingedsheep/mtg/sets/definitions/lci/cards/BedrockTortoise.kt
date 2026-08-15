package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AssignDamageEqualToToughness
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bedrock Tortoise
 * {3}{G}
 * Creature — Turtle
 * 0/6
 *
 * During your turn, creatures you control have hexproof.
 * Each creature you control with toughness greater than its power assigns combat damage equal to
 * its toughness rather than its power.
 *
 * Two statics:
 *  1. [ConditionalStaticAbility] — [IsYourTurn] gates [GrantKeyword](HEXPROOF, AllCreaturesYouControl).
 *     While it is the Tortoise controller's turn, all creatures they control gain hexproof from
 *     opponents.
 *  2. [AssignDamageEqualToToughness](AllCreaturesYouControl, onlyWhenToughnessGreaterThanPower = true).
 *     For each creature the controller controls whose toughness exceeds its power (e.g. the Tortoise
 *     itself at 0/6, or any other high-toughness creature), combat damage is assigned equal to
 *     toughness rather than power.
 */
val BedrockTortoise = card("Bedrock Tortoise") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Turtle"
    power = 0
    toughness = 6
    oracleText = "During your turn, creatures you control have hexproof.\n" +
        "Each creature you control with toughness greater than its power assigns combat damage " +
        "equal to its toughness rather than its power."

    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(Keyword.HEXPROOF, GroupFilter.AllCreaturesYouControl)
    }

    staticAbility {
        ability = AssignDamageEqualToToughness(
            filter = GroupFilter.AllCreaturesYouControl,
            onlyWhenToughnessGreaterThanPower = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "176"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/701bd623-3100-44dc-adec-53fa3a95ab19.jpg?1782694468"
    }
}
