package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vengeful Bloodwitch
 * {1}{B}
 * Creature — Vampire Warlock
 * 1/1
 *
 * Whenever this creature or another creature you control dies, target opponent loses 1 life
 * and you gain 1 life.
 *
 * "This creature or another creature you control" is exactly "a creature you control" (it
 * includes the source itself), so the trigger is [Triggers.YourCreatureDies] — it fires off
 * the source's own death via last-known information just as for any other controlled creature.
 */
val VengefulBloodwitch = card("Vengeful Bloodwitch") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warlock"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature or another creature you control dies, target opponent " +
        "loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            Effects.LoseLife(1, opponent),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Jarel Threat"
        flavorText = "\"You thought I was unarmed? Poor fool—your veins are my arsenal.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd0c12dd-f138-45c0-9614-d83a1d8e8399.jpg?1782689199"
    }
}
