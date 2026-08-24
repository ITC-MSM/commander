package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Erstwhile Trooper
 * {1}{B}{G}
 * Creature — Zombie Soldier
 * 2/2
 * Discard a creature card: This creature gets +2/+2 and gains trample until end of turn. Activate only once each turn.
 */
val ErstwhileTrooper = card("Erstwhile Trooper") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Soldier"
    oracleText = "Discard a creature card: This creature gets +2/+2 and gains trample until end of turn. Activate only once each turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Jason A. Engle"
        flavorText = "\"The Erstwhile—rotten of body and outmoded in dress, but unfailing in loyalty.\"\n—Vraska"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/351ee4ba-882d-48c1-837d-e9eccc5bc50d.jpg?1783934137"
    }
}
