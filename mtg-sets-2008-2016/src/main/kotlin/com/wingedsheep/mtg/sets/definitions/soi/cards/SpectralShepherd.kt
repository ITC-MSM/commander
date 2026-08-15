package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Spectral Shepherd
 * {2}{W}
 * Creature — Spirit
 * 2/2
 * Flying
 * {1}{U}: Return target Spirit you control to its owner's hand.
 */
val SpectralShepherd = card("Spectral Shepherd") {
    manaCost = "{2}{W}"
    colorIdentity = "WU"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 2
    oracleText = "Flying\n{1}{U}: Return target Spirit you control to its owner's hand."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        target = TargetObject(
            filter = TargetFilter(GameObjectFilter.Creature.youControl().withSubtype("Spirit"))
        )
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Johann Bodin"
        flavorText = "The restless geists of common folk often continue their lifelong labors."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac355ae2-7553-4b51-bcce-1c4168dad0d6.jpg?1782712135"
    }
}
