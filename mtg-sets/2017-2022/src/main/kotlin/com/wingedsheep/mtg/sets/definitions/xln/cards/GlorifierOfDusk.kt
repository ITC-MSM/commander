package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glorifier of Dusk
 * {3}{W}{W}
 * Creature — Vampire Soldier
 * 4/4
 *
 * Pay 2 life: This creature gains flying until end of turn.
 * Pay 2 life: This creature gains vigilance until end of turn.
 */
val GlorifierOfDusk = card("Glorifier of Dusk") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "Pay 2 life: This creature gains flying until end of turn.\n" +
        "Pay 2 life: This creature gains vigilance until end of turn."
    power = 4
    toughness = 4

    activatedAbility {
        cost = Costs.PayLife(2)
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.PayLife(2)
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "12"
        artist = "Viktor Titov"
        flavorText = "\"The blood of the enemy is a sacrament. The strength it gives is proof that our cause is just.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aaf87f95-1c77-455a-8feb-57bfd4d159c9.jpg"
    }
}
