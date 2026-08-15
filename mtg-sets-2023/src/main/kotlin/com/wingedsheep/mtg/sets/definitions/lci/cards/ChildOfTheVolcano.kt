package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Child of the Volcano — {3}{R}
 * Creature — Elemental
 * 3/3
 *
 * Trample
 * At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature.
 * (You descended if a permanent card was put into your graveyard from anywhere.)
 *
 * "Descended this turn" is CR 700.11: at least one nontoken permanent card was put into
 * your graveyard from any zone this turn. The intervening-if gate fires only once per end
 * step regardless of how many times you descended, and cannot trigger if you have not yet
 * descended as the end step begins.
 */
val ChildOfTheVolcano = card("Child of the Volcano") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature. " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Uriah Voth"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e964f026-9cbf-4fa2-acdc-60d19b88f183.jpg?1782694498"
        ruling("2023-11-10", "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn.")
        ruling("2023-11-10", "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended.")
        ruling("2023-11-10", "Abilities that begin with \"At the beginning of your end step, if you descended this turn\" will trigger only once during your end step, no matter how many times you descended this turn. However, if you haven't descended this turn as your end step begins, the ability won't trigger at all. It's not possible to put a permanent card into your graveyard during the end step in time to have the ability trigger.")
    }
}
