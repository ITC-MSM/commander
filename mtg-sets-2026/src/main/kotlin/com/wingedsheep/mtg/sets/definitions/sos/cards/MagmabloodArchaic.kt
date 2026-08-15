package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.convergeEntersWithCounters
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Magmablood Archaic
 * {2/R}{2/R}{2/R}
 * Creature — Avatar
 * 2/2
 *
 * Trample, reach
 * Converge — This creature enters with a +1/+1 counter on it for each color of mana spent to cast it.
 * Whenever you cast an instant or sorcery spell, creatures you control get +1/+0 until end of turn
 * for each color of mana spent to cast that spell.
 *
 * The {2/R} hybrid cost makes the colour count entirely a function of how you pay: all generic/red
 * still counts as (at most) one colour, but multicolour payment pushes the Converge counters and the
 * spell-trigger pump higher. The enter-with-counters part is [convergeEntersWithCounters] (reads this
 * creature's own cast via [DynamicAmount.DistinctColorsManaSpent]); the spell-cast pump reads the
 * *triggering* spell's colour count via [DynamicAmounts.colorsSpentOnTriggeringSpell] (the
 * `ContextPropertyKey.COLORS_SPENT_ON_TRIGGERING_SPELL` captured from `SpellCastEvent`).
 */
val MagmabloodArchaic = card("Magmablood Archaic") {
    manaCost = "{2/R}{2/R}{2/R}"
    colorIdentity = "R"
    typeLine = "Creature — Avatar"
    power = 2
    toughness = 2
    oracleText = "Trample, reach\n" +
        "Converge — This creature enters with a +1/+1 counter on it for each color of mana spent " +
        "to cast it.\n" +
        "Whenever you cast an instant or sorcery spell, creatures you control get +1/+0 until end " +
        "of turn for each color of mana spent to cast that spell."

    keywords(Keyword.TRAMPLE, Keyword.REACH)

    convergeEntersWithCounters()

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Patterns.Group.modifyStatsForAll(
            power = DynamicAmounts.colorsSpentOnTriggeringSpell(),
            toughness = DynamicAmount.Fixed(0),
            filter = Filters.Group.creaturesYouControl,
        )
        description = "Whenever you cast an instant or sorcery spell, creatures you control get " +
            "+1/+0 until end of turn for each color of mana spent to cast that spell."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d611278-9948-4345-b4dd-aa6eaf21b233.jpg?1775937816"
    }
}
