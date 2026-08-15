package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.FaceDownLookScope
import com.wingedsheep.sdk.scripting.effects.LookAtFaceDownEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Smoke Teller
 * {1}{G}
 * Creature — Human Shaman
 * 2/2
 * {1}{U}: Look at target face-down creature.
 */
val SmokeTeller = card("Smoke Teller") {
    manaCost = "{1}{G}"
    colorIdentity = "UG"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{1}{U}: Look at target face-down creature."

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        val t = target("target", TargetPermanent(
            filter = TargetFilter(GameObjectFilter.Creature.faceDown())
        ))
        effect = LookAtFaceDownEffect(t, FaceDownLookScope.SINGLE_TARGET)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Min Yum"
        flavorText = "\"See your enemies from a thousand sides. Then you will find a thousand ways to kill them.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/9/1916a2d9-4747-4118-b1b2-7a5ce492e3fc.jpg?1562783190"
    }
}
