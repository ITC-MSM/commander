package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Marsh Flitter
 * {3}{B}
 * Creature — Faerie Rogue
 * 1/1
 * Flying
 * When this creature enters, create two 1/1 black Goblin Rogue creature tokens.
 * Sacrifice a Goblin: This creature has base power and toughness 3/3 until end of turn.
 *
 * The two Goblins it brings are the fodder for its own ability, but the cost is any Goblin — and
 * the bare tribal noun means any Goblin *permanent* you control, not just creatures.
 *
 * "Has base power and toughness 3/3" *sets* the base (layer 7b) rather than pumping, so a second
 * activation is redundant while the first is still in effect, and +1/+1 counters still apply on top
 * of it. The Flitter is a Faerie, never a Goblin, so it can't feed itself.
 */
val MarshFlitter = card("Marsh Flitter") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Rogue"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, create two 1/1 black Goblin Rogue creature " +
        "tokens.\nSacrifice a Goblin: This creature has base power and toughness 3/3 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Goblin", "Rogue"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/f/4/f44d5271-5d10-46b2-9ba2-5788d99de2e6.jpg?1783942839",
        )
        description = "create two 1/1 black Goblin Rogue creature tokens."
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
        effect = Effects.SetBasePowerAndToughness(
            power = 3,
            toughness = 3,
            target = EffectTarget.Self,
            duration = Duration.EndOfTurn,
        )
        description = "Sacrifice a Goblin: This creature has base power and toughness 3/3 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/040e1039-1943-4c2d-aa98-b7f9519de321.jpg?1783942888"
    }
}
