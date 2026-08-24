package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hoar Shade
 * {3}{B}
 * Creature — Shade
 * 1/2
 *
 * {B}: This creature gets +1/+1 until end of turn.
 *
 * The classic Shade pump, identical in shape to Looming Shade: a mana-only activated ability whose
 * effect is `Effects.ModifyStats` onto `EffectTarget.Self`, taking the facade's default
 * `Duration.EndOfTurn`.
 */
val HoarShade = card("Hoar Shade") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 1
    toughness = 2
    oracleText = "{B}: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Richard Thomas"
        flavorText = "\"The creature we fought in the western waste was doubly dangerous: mortally wounded, it rebounded and attacked again.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72242dff-15ca-4da0-b3ae-9984d037b31f.jpg"
    }
}
