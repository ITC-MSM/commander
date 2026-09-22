package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

val LilianaTheFaultless = card("Liliana the Faultless") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    oracleText = "Whenever another creature or planeswalker you control enters, you gain 1 life.\n" +
        "{1}, {T}, Discard a card: Another target creature or planeswalker you control gains hexproof until end of turn."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.CreatureOrPlaneswalker.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.GainLife(1)
        description = "Whenever another creature or planeswalker you control enters, you gain 1 life."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.DiscardCard)
        val permanent = target(
            "another target creature or planeswalker you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.youControl()).other()),
        )
        effect = Effects.GrantKeyword(Keyword.HEXPROOF, permanent)
        description = "Another target creature or planeswalker you control gains hexproof until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "200"
        artist = "Marta Nael"
        flavorText = "\"All mistakes can be fixed. Even you.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70d8c400-87dc-4f15-808f-e54a95d779fc.jpg?1788329194"
        inBooster = false
    }
}
