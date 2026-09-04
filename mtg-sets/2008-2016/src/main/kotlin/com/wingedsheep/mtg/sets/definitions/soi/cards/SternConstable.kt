package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stern Constable (Shadows over Innistrad #39)
 * {W}
 * Creature — Human Soldier
 * 1 / 1
 *
 * {T}, Discard a card: Tap target creature.
 */
val SternConstable = card("Stern Constable") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "{T}, Discard a card: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.DiscardCard)
        val t = target("target", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Svetlin Velinov"
        flavorText = "\"I'm sure you have a story. Everyone has a story. You can tell it to the bars.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f0f87d4-5fed-415c-918a-c3546697a3da.jpg?1783937809"
    }
}
