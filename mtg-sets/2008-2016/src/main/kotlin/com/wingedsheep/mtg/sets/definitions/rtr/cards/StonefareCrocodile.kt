package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stonefare Crocodile
 * {2}{G}
 * Creature — Crocodile
 * 3/2
 *
 * {2}{B}: This creature gains lifelink until end of turn. (Damage dealt by this creature also causes you to gain that much life.)
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * An off-colour keyword grant on itself, defaulting to end of turn.
 */
val StonefareCrocodile = card("Stonefare Crocodile") {
    manaCost = "{2}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Crocodile"
    oracleText = "{2}{B}: This creature gains lifelink until end of turn. (Damage dealt by this creature also causes you to gain that much life.)"
    power = 3
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Tomasz Jedruszek"
        flavorText = "The Izzet's plans to exploit the undercity ran into a few stubborn obstacles."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2517d74-0589-49dc-88f1-1fc02b27bc9d.jpg?1783940346"
    }
}
