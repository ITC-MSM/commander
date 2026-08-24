package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rootwalla
 * {2}{G}
 * Creature — Lizard
 * 2/2
 * {1}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn.
 */
val Rootwalla = card("Rootwalla") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lizard"
    power = 2
    toughness = 2
    oracleText = "{1}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "252"
        artist = "Roger Raupp"
        flavorText = "If you try to sneak up on a rootwalla, you'll suddenly find yourself dealing with twice the lizard."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03ce4d5d-63cb-47b6-94ce-2063977db9b4.jpg"
    }
}
