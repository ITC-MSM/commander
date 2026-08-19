package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flamekin Brawler
 * {R}
 * Creature — Elemental Warrior
 * 0/2
 * {R}: This creature gets +1/+0 until end of turn.
 */
val FlamekinBrawler = card("Flamekin Brawler") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Warrior"
    power = 0
    toughness = 2
    oracleText = "{R}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Daren Bader"
        flavorText = "When he hits people, they stay hit."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/301abe8f-916a-4e5b-bf25-f1e12ae9cee2.jpg?1783942876"
    }
}
