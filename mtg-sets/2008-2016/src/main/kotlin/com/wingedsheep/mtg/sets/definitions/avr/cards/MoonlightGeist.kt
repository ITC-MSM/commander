package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Moonlight Geist
 * {2}{W}
 * Creature — Spirit
 * 2 / 1
 *
 * Flying
 * {3}{W}: Prevent all combat damage that would be dealt to and dealt by this creature this turn.
 *
 * Urborg Phantom's activated fog-on-a-body: [Effects.PreventCombatDamageToAndBy] is the shield
 * with `scope = CombatOnly` and `direction = Both` already baked in, so only the recipient
 * ([EffectTarget.Self]) has to be named.
 */
val MoonlightGeist = card("Moonlight Geist") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "{3}{W}: Prevent all combat damage that would be dealt to and dealt by this creature this turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        effect = Effects.PreventCombatDamageToAndBy(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Dan Murayama Scott"
        flavorText = "Wails and whispers are the only weapons she has left."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cf4c4cf-df35-4725-81ca-d62b70b8d0dd.jpg?1783940730"
    }
}
