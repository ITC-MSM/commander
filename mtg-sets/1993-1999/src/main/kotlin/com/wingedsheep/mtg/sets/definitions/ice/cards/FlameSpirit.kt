package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flame Spirit
 * {4}{R}
 * Creature — Elemental Spirit
 * 2/3
 *
 * {R}: This creature gets +1/+0 until end of turn.
 *
 * Plain firebreathing: a mana-only activated ability whose effect is `Effects.ModifyStats` onto
 * `EffectTarget.Self`, taking the facade's default `Duration.EndOfTurn`.
 */
val FlameSpirit = card("Flame Spirit") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Spirit"
    power = 2
    toughness = 3
    oracleText = "{R}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "184"
        artist = "Justin Hampton"
        flavorText = "\"The spirit of the flame is the spirit of change.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/add2b82a-9aa5-4d5c-a1c2-e313541f12c8.jpg"
    }
}
