package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Referee Squad
 * {2}{U}
 * Creature — Homunculus
 * 2/2
 * Convoke
 * Vigilance
 * When this creature enters, tap target creature an opponent controls and put a stun counter on it.
 *
 * The target may already be tapped (printed ruling) — the tap simply does nothing and the stun
 * counter still lands, so the requirement stays a plain "creature an opponent controls".
 */
val RefereeSquad = card("Referee Squad") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Homunculus"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Vigilance\n" +
        "When this creature enters, tap target creature an opponent controls and put a stun " +
        "counter on it. (If a permanent with a stun counter would become untapped, remove one " +
        "from it instead.)"
    power = 2
    toughness = 2

    keywords(Keyword.CONVOKE, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.Creature.opponentControls())
        )
        effect = Effects.Tap(victim) then Effects.AddCounters(Counters.STUN, 1, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "327"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/434515bf-de57-4c00-b0b4-c9579cc1b84c.jpg?1783916905"
        ruling(
            "2023-04-14",
            "Referee Squad's enters-the-battlefield ability can target a creature that's already tapped."
        )
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
