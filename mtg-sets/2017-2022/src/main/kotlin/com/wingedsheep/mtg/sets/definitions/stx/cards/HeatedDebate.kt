package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Heated Debate — Strixhaven: School of Mages #106 (canonical printing)
 * {2}{R} · Instant
 *
 * This spell can't be countered. (This includes by the ward ability.)
 * Heated Debate deals 4 damage to target creature or planeswalker.
 *
 * The Urza's Rage shape without the kicker: `cantBeCountered` is a flag on the card itself — the
 * engine reads it wherever a spell would be countered, which is what makes it beat ward — and the
 * body is a single [Effects.DealDamage] of 4 to a [Targets.CreatureOrPlaneswalker] target.
 */
val HeatedDebate = card("Heated Debate") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText =
        "This spell can't be countered. (This includes by the ward ability.)\n" +
        "Heated Debate deals 4 damage to target creature or planeswalker."

    cantBeCountered = true

    spell {
        val victim = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(4, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Bayard Wu"
        flavorText = "\"While you were wasting time with abstract equations, I mastered ancient Oggyar fire magic. Your move.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f40c88e2-28ed-4e5b-bcb3-4397d6a15a6f.jpg?1783927353"
    }
}
