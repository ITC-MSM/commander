package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wydwen, the Biting Gale
 * {2}{U}{B}
 * Legendary Creature — Faerie Wizard
 * 3/3
 * Flash
 * Flying
 * {U}{B}, Pay 1 life: Return Wydwen to its owner's hand.
 */
val WydwenTheBitingGale = card("Wydwen, the Biting Gale") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Faerie Wizard"
    power = 3
    toughness = 3
    oracleText = "Flash\nFlying\n{U}{B}, Pay 1 life: Return Wydwen to its owner's hand."

    keywords(Keyword.FLASH, Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{B}"), Costs.PayLife(1))
        effect = Effects.ReturnToHand(EffectTarget.Self)
        description = "{U}{B}, Pay 1 life: Return Wydwen to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Matt Cavotta"
        flavorText = "In a world of bright, cloudless skies, she is the dark storm on the horizon."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff6b558b-74a5-497d-8be4-474c375d7ca7.jpg?1783942852"
    }
}
