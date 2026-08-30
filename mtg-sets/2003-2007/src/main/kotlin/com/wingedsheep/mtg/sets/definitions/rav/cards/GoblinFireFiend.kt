package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.MustBeBlocked
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Goblin Fire Fiend
 * {3}{R}
 * Creature — Goblin Berserker
 * 1/1
 *
 * Haste
 * This creature must be blocked if able.
 * {R}: This creature gets +1/+0 until end of turn.
 *
 * "Must be blocked if able" is the *at least one* form of [MustBeBlocked] (`allCreatures = false`),
 * not the Lure-style "blocked by all creatures able to block it": per the printed ruling the
 * defending player must assign **a** blocker, and is free to keep the rest back. `BlockPhaseManager`
 * enforces it during the declare-blockers legality check.
 */
val GoblinFireFiend = card("Goblin Fire Fiend") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Berserker"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "This creature must be blocked if able.\n" +
        "{R}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.HASTE)

    staticAbility {
        ability = MustBeBlocked()
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64d3b2d2-4ec2-495d-833d-7476cbfc80f6.jpg?1783943654"
        ruling(
            "2005-10-01",
            "If Goblin Fire Fiend is attacking, the defending player must assign at least one " +
                "blocker to it during the declare blockers step if that player controls any " +
                "creatures that could block Goblin Fire Fiend."
        )
    }
}
