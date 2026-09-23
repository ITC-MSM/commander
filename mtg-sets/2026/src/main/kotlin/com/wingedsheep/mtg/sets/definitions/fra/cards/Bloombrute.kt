package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val Bloombrute = card("Bloombrute") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Plant Elemental"
    power = 4
    toughness = 4
    oracleText = "Whenever you gain life, draw a card. This ability triggers only once each turn.\n" +
        "{4}{G}{W}: Target creature gains trample and lifelink until end of turn."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Mana("{4}{G}{W}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.TRAMPLE, t),
            Effects.GrantKeyword(Keyword.LIFELINK, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Antonio José Manzanedo"
        flavorText = "\"Proper medical assistance is hard to find. I suggest you grow your own.\"\n—Karalgin, professor of phytomancy"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b804503-9c70-4b1f-bb13-a65fb6dd3ef8.jpg?1789644843"
        inBooster = false
    }
}
