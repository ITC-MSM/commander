package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Devout Chaplain
 * {2}{W}
 * Creature — Human Cleric
 * 2 / 2
 *
 * {T}, Tap two untapped Humans you control: Exile target artifact or enchantment.
 *
 * "Two untapped Humans you control" is a bare tribal noun — any Human *permanent*, not only
 * creatures. `excludeSelf = true` because the Chaplain is itself a Human and is already paying
 * the `{T}` half of the cost, so it can't also be one of the two tapped Humans.
 */
val DevoutChaplain = card("Devout Chaplain") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "{T}, Tap two untapped Humans you control: Exile target artifact or enchantment."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.TapPermanents(
                count = 2,
                filter = GameObjectFilter.Permanent.withSubtype(Subtype.HUMAN),
                excludeSelf = true
            )
        )
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Lucas Graciano"
        flavorText = "\"By Avacyn's decree, we shall cleanse these relics of their demonic past.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84ceb7f1-14b7-4102-ade2-fbeb835d3804.jpg?1783940736"
    }
}
