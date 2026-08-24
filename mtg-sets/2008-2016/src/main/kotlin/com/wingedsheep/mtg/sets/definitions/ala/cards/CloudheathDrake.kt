package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cloudheath Drake
 * {4}{U}
 * Artifact Creature — Drake
 * 3 / 3
 * Flying
 * {1}{W}: This creature gains vigilance until end of turn.
 *
 * An Esper artifact creature with one of the cycle's off-colour activations. Flying is a plain
 * keyword; the grant is untargeted — "this creature" is the source — so it is
 * [Effects.GrantKeyword] on [EffectTarget.Self], whose default `Duration.EndOfTurn` is the printed
 * "until end of turn".
 */
val CloudheathDrake = card("Cloudheath Drake") {
    manaCost = "{4}{U}"
    colorIdentity = "UW"
    typeLine = "Artifact Creature — Drake"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{1}{W}: This creature gains vigilance until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Izzy"
        flavorText = "A permanent storm rages over the plain of Cloudheath, and drakes ride its currents—two reminders that some elements of Esper will not be controlled."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f71ec76-47e1-4f55-bc57-e7b1c88baedd.jpg"
    }
}
