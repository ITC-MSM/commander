package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Strongarm Monk
 * {4}{W}
 * Creature — Human Monk
 * 3 / 3
 *
 * Whenever you cast a noncreature spell, creatures you control get +1/+1 until end of turn.
 *
 * A team pump rather than a self pump, so this is not prowess — a plain
 * [Triggers.YouCastNoncreature] trigger over [Patterns.Group.modifyStatsForAll], which iterates the
 * group and applies the +1/+1 per creature (the source included; the printed noun is "creatures you
 * control", not "other creatures"). The `Duration.EndOfTurn` default spells "until end of turn".
 */
val StrongarmMonk = card("Strongarm Monk") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Monk"
    power = 3
    toughness = 3
    oracleText = "Whenever you cast a noncreature spell, creatures you control get +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Viktor Titov"
        flavorText = "\"His companions are wise to follow him, for his foes dare not stand in his way.\"\n—Zhiada, Dirgur protector"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f5f5ead-b9e0-44c0-893f-0e3ae01933d3.jpg?1783938611"
    }
}
