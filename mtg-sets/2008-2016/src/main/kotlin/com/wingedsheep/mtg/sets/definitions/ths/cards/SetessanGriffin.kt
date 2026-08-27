package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Setessan Griffin
 * {4}{W}
 * Creature — Griffin
 * 3 / 2
 *
 * Flying
 * {2}{G}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn.
 */
val SetessanGriffin = card("Setessan Griffin") {
    manaCost = "{4}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Griffin"
    power = 3
    toughness = 2
    oracleText = "Flying\n{2}{G}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{2}{G}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Greg Staples"
        flavorText = "Most griffins must be caught and broken into the service of the polis. Not so in Setessa, where they volunteer."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35d2ae77-b16c-4a01-84ce-5c78be5a54d8.jpg"
    }
}
