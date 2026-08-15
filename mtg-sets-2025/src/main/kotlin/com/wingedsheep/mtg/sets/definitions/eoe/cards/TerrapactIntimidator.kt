package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ChooseActionEffect
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Terrapact Intimidator
 * {1}{R}
 * Creature — Kavu Scout
 * When this creature enters, target opponent may have you create two Lander tokens.
 * If they don't, put two +1/+1 counters on this creature.
 * 2/1
 *
 * Modeled as a ChooseActionEffect: target opponent picks between "create two Landers
 * for you" or "put two +1/+1 counters on Terrapact". Both options are always feasible.
 */
val TerrapactIntimidator = card("Terrapact Intimidator") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu Scout"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, target opponent may have you create two Lander tokens. " +
        "If they don't, put two +1/+1 counters on this creature. " +
        "(A Lander token is an artifact with \"{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = ChooseActionEffect(
            choices = listOf(
                EffectChoice(
                    label = "Have them create two Lander tokens",
                    effect = Effects.CreateLander(count = 2)
                ),
                EffectChoice(
                    label = "Put two +1/+1 counters on this creature",
                    effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
                )
            ),
            player = opponent
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Slawomir Maniak"
        flavorText = "\"Your land, or my blades.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13fe3358-fd68-4245-a2ad-aa9200cf4655.jpg?1752947219"
    }
}
