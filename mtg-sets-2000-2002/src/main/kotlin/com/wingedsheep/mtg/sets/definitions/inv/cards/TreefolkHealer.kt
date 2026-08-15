package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Treefolk Healer
 * {4}{G}
 * Creature — Treefolk Cleric
 * 2/3
 * {2}{W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn.
 */
val TreefolkHealer = card("Treefolk Healer") {
    manaCost = "{4}{G}"
    colorIdentity = "GW"
    typeLine = "Creature — Treefolk Cleric"
    power = 2
    toughness = 3
    oracleText = "{2}{W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.Tap)
        val t = target("target", AnyTarget())
        effect = Effects.PreventNextDamage(2, t)
        description = "{2}{W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "218"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73c6f5c0-686d-4b3a-add7-487f9fff5faa.jpg?1562918162"
    }
}
