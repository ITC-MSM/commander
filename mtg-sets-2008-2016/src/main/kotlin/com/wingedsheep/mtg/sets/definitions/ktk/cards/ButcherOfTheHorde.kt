package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Butcher of the Horde
 * {1}{R}{W}{B}
 * Creature — Demon
 * 5/4
 * Flying
 * Sacrifice another creature: Butcher of the Horde gains your choice of vigilance, lifelink,
 * or haste until end of turn.
 */
val ButcherOfTheHorde = card("Butcher of the Horde") {
    manaCost = "{1}{R}{W}{B}"
    colorIdentity = "WBR"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 4
    oracleText = "Flying\nSacrifice another creature: This creature gains your choice of vigilance, lifelink, or haste until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.Creature)
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self),
                "This creature gains vigilance until end of turn"
            ),
            Mode.noTarget(
                Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
                "This creature gains lifelink until end of turn"
            ),
            Mode.noTarget(
                Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self),
                "This creature gains haste until end of turn"
            )
        )
        description = "Sacrifice another creature: This creature gains your choice of vigilance, lifelink, or haste until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "168"
        artist = "Karl Kopinski"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c76027b-9c8d-4181-9311-270fed0212e3.jpg?1562786278"
    }
}
