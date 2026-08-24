package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shambling Strider
 * {4}{G}{G}
 * Creature — Yeti
 * 5/5
 *
 * {R}{G}: This creature gets +1/-1 until end of turn.
 *
 * Firebreathing that trades toughness for power — one `Effects.ModifyStats` onto
 * `EffectTarget.Self` carries both halves, so the negative toughness modifier needs no separate
 * effect. The off-colour {R} in the activation cost is why the colour identity is GR.
 */
val ShamblingStrider = card("Shambling Strider") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Yeti"
    power = 5
    toughness = 5
    oracleText = "{R}{G}: This creature gets +1/-1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}{G}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "263"
        artist = "Douglas Shuler"
        flavorText = "Freyalise forbid that any stranger should wander into the Striders' territory."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/8886ba2d-b25a-4b74-9299-911c509ae864.jpg"
    }
}
