package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Genemorph Imago
 * {G}{U}
 * Creature — Insect Druid
 * 1/3
 *
 * Flying
 * Landfall — Whenever a land you control enters, target creature has base power and toughness 3/3
 * until end of turn. If you control six or more lands, that creature has base power and toughness
 * 6/6 until end of turn instead.
 */
val GenemorphImago = card("Genemorph Imago") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Insect Druid"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "Landfall — Whenever a land you control enters, target creature has base power and toughness " +
        "3/3 until end of turn. If you control six or more lands, that creature has base power and " +
        "toughness 6/6 until end of turn instead."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        val t = target("target creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = Conditions.ControlLandsAtLeast(6),
            effect = Effects.SetBasePowerAndToughness(6, 6, t, Duration.EndOfTurn),
            elseEffect = Effects.SetBasePowerAndToughness(3, 3, t, Duration.EndOfTurn)
        )
        description = "Landfall — Whenever a land you control enters, target creature has base power " +
            "and toughness 3/3 until end of turn. If you control six or more lands, that creature has " +
            "base power and toughness 6/6 until end of turn instead."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Brian Valeza"
        flavorText = "\"We can all be more than we are. Let me help.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40f0ecf7-f49e-46ff-aa5b-9ff5361b72c5.jpg?1752947444"

        ruling(
            "2025-07-25",
            "The effect of Genemorph Imago's landfall ability will overwrite any previous effects that " +
                "set the creature's power and toughness to specific values. Effects that otherwise modify " +
                "the target creature's power and toughness will still apply no matter when they took " +
                "effect. The same is true for +1/+1 counters."
        )
    }
}
