package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inkrise Infiltrator — Kamigawa: Neon Dynasty #100 (canonical printing)
 * {1}{B} · Creature — Human Ninja · 1/2
 *
 * Flying
 * {3}{B}: This creature gets +2/+2 until end of turn.
 */
val InkriseInfiltrator = card("Inkrise Infiltrator") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Ninja"
    power = 1
    toughness = 2
    oracleText = "Flying\n{3}{B}: This creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{3}{B}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "{3}{B}: This creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Campbell White"
        flavorText = "\"They can't follow your footprints if you never touch the ground.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbb6e447-5f40-4039-8a17-257b4a55382c.jpg?1783923885"
    }
}
