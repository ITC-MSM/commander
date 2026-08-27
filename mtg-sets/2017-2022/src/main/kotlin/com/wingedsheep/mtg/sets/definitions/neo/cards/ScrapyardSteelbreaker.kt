package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scrapyard Steelbreaker — Kamigawa: Neon Dynasty #160 (canonical printing)
 * {3}{R} · Artifact Creature — Human Warrior · 3/4
 *
 * {1}, Sacrifice another artifact: This creature gets +2/+1 until end of turn.
 *
 * [Costs.SacrificeAnother] rather than a filtered [Costs.Sacrifice] — the Steelbreaker is itself an
 * artifact, and "another" has to exclude it or the ability could eat its own source.
 */
val ScrapyardSteelbreaker = card("Scrapyard Steelbreaker") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Human Warrior"
    power = 3
    toughness = 4
    oracleText = "{1}, Sacrifice another artifact: This creature gets +2/+1 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeAnother(GameObjectFilter.Artifact))
        effect = Effects.ModifyStats(2, 1, EffectTarget.Self)
        description = "{1}, Sacrifice another artifact: This creature gets +2/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Eric Wilkerson"
        flavorText = "\"You've got about three seconds to get out of here before you get a " +
            "faceful of metal.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/7/5776e576-5562-45ea-a29b-185410317e17.jpg?1783923860"
    }
}
