package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Riot Ringleader
 * {2}{R}
 * Creature — Human Warrior
 * 2 / 2
 *
 * Whenever this creature attacks, Human creatures you control get +1/+0 until end of turn.
 *
 * [Triggers.Attacks] is SELF-bound, so only the Ringleader's own attack fires it. The pump is
 * [Patterns.Group.modifyStatsForAll] over the Humans you control — a group iteration rather than a
 * static lord, so the bonus is locked to the creatures present on resolution and expires at end of
 * turn. The Ringleader is itself a Human, so it pumps too.
 */
val RiotRingleader = card("Riot Ringleader") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature attacks, Human creatures you control get +1/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Group.modifyStatsForAll(
            1, 0,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Human").youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Gabor Szikszai"
        flavorText = "\"So the vampires like hot blood, do they? Let's see how they handle ours.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c043f30b-548f-4c31-a415-0e59c2841dcf.jpg?1783940678"
    }
}
