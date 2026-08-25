package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gravel-Hide Goblin — Ravnica Allegiance #105
 * {1}{R} · Creature — Goblin Shaman · 2 / 1
 *
 * An off-colour activation ({G} on a red creature) — the mana cost is just a string, so
 * nothing special is needed to express the Gruul "splash" cycle.
 */
val GravelHideGoblin = card("Gravel-Hide Goblin") {
    manaCost = "{1}{R}"
    colorIdentity = "GR"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 1
    oracleText = "{3}{G}: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Jonathan Kuo"
        flavorText = "\"No peace accord will save Ravnica. You don't build on rot. You burn it down and start again.\"\n" +
        "—Domri Rade"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/4942068c-ffde-4a6b-849e-8acf05e1d2e1.jpg"
    }
}
