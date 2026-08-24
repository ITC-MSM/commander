package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Passwall Adept
 * {1}{U}
 * Creature — Human Wizard
 * 1/3
 * {2}{U}: Target creature can't be blocked this turn.
 */
val PasswallAdept = card("Passwall Adept") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "{2}{U}: Target creature can't be blocked this turn."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "John Thacker"
        flavorText = "\"My doors are called trespassing, my signatures, forgeries. They don't respect my talents, and I don't respect their labels.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8ed28c3-8c66-4883-8774-67ac5ab9e81c.jpg?1783934185"
    }
}
