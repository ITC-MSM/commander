package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Felidar Retreat — ZNR #16
 * Enchantment — Rare
 *
 * Landfall — Whenever a land you control enters, choose one —
 * • Create a 2/2 white Cat Beast creature token.
 * • Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until
 *   end of turn.
 *
 * The second mode iterates the group once and applies both halves per creature, so the
 * "those creatures" that gain vigilance are exactly the ones that received a counter — the
 * set is fixed at resolution (ZNR ruling: creatures you begin to control later in the turn
 * get neither).
 */
val FelidarRetreat = card("Felidar Retreat") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Landfall — Whenever a land you control enters, choose one —\n" +
        "• Create a 2/2 white Cat Beast creature token.\n" +
        "• Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Cat", "Beast"),
                    imageUri = "https://cards.scryfall.io/normal/front/e/2/e2c91781-acf9-4cff-be1a-85148ad2a683.jpg?1783929500",
                ),
                "Create a 2/2 white Cat Beast creature token",
            ),
            Mode.noTarget(
                Effects.ForEachInGroup(
                    filter = GroupFilter.AllCreaturesYouControl,
                    effect = Effects.Composite(
                        Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                        Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self, Duration.EndOfTurn),
                    ),
                ),
                "Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until end of turn",
            ),
        )
        description = "Landfall — Whenever a land you control enters, choose one — " +
            "create a 2/2 white Cat Beast creature token; or put a +1/+1 counter on each " +
            "creature you control and those creatures gain vigilance until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Ralph Horsley"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45340647-4d3e-4be1-b0e6-e40cc56a438b.jpg?1783929418"

        ruling(
            "2020-09-25",
            "Felidar Retreat's second mode affects only creatures you control at the time the ability " +
                "resolves, including creatures you control but that for some reason didn't get a +1/+1 " +
                "counter. Creatures you begin to control later in the turn won't gain vigilance or get a " +
                "+1/+1 counter.",
        )
        ruling(
            "2024-11-08",
            "A landfall ability doesn't trigger if a permanent already on the battlefield becomes a land.",
        )
    }
}
