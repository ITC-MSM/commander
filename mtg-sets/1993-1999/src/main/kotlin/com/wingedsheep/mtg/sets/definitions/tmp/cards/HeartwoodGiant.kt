package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Heartwood Giant
 * {3}{G}{G}
 * Creature — Giant
 * 4/4
 * {T}, Sacrifice a Forest: This creature deals 2 damage to target player or planeswalker.
 */
val HeartwoodGiant = card("Heartwood Giant") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 4
    oracleText = "{T}, Sacrifice a Forest: This creature deals 2 damage to target player or planeswalker."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.FOREST))
        )
        val victim = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "232"
        artist = "Randy Elliott"
        flavorText = "Wind in the trees is soothing, but the same can't be said for trees on the wind."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4baacffe-76d1-4cfb-a047-d6d126bb8de0.jpg"
    }
}
