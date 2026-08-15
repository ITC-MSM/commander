package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Daru Healer
 * {2}{W}
 * Creature — Human Cleric
 * 1/2
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 * Morph {W} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 */
val DaruHealer = card("Daru Healer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 2
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to any target this turn.\nMorph {W}"

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", AnyTarget())
        effect = Effects.PreventNextDamage(1, t)
    }

    morph = "{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Dany Orizio"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e4f3eff-ac99-41e2-9003-9630cdb3ae23.jpg?1562898282"
    }
}
