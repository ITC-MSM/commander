package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Mordor Trebuchet
 * {2}{B}
 * Artifact Creature — Wall
 * 1/4
 *
 * Defender
 * Whenever you attack with one or more Goblins and/or Orcs, create a 2/1 colorless Construct
 * artifact creature token with flying named Ballistic Boulder that's tapped and attacking.
 * Sacrifice that token at end of combat.
 */
val MordorTrebuchet = card("Mordor Trebuchet") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Wall"
    power = 1
    toughness = 4
    oracleText = "Defender\n" +
        "Whenever you attack with one or more Goblins and/or Orcs, create a 2/1 colorless Construct artifact creature token with flying named Ballistic Boulder that's tapped and attacking. Sacrifice that token at end of combat."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.YouAttackWithFilter(
            GameObjectFilter.Creature.youControl().withAnySubtype("Goblin", "Orc")
        )
        effect = CreateTokenEffect(
            power = 2,
            toughness = 1,
            colors = setOf(), // colorless
            creatureTypes = setOf("Construct"),
            keywords = setOf(Keyword.FLYING),
            name = "Ballistic Boulder",
            artifactToken = true,
            tapped = true,
            attacking = true,
            exileAtStep = Step.END_COMBAT,
            imageUri = "https://cards.scryfall.io/normal/front/3/8/389a6f88-0c90-4b67-a72c-7e9674f00898.jpg?1699974407"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Alexander Forssberg"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/648bc8ae-1798-4c2f-a372-0487a90ba4d3.jpg?1686968597"
    }
}
