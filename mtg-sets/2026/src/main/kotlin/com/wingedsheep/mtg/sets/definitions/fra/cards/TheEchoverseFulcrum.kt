package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

val TheEchoverseFulcrum = card("The Echoverse Fulcrum") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "When The Echoverse Fulcrum enters, draw a card, then discard a card.\n" +
        "{5}, {T}, Exile The Echoverse Fulcrum: Destroy all creatures. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
        description = "When The Echoverse Fulcrum enters, draw a card, then discard a card."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.ExileSelf)
        timing = TimingRule.SorcerySpeed
        effect = Effects.DestroyAll(GameObjectFilter.Creature)
        description = "{5}, {T}, Exile The Echoverse Fulcrum: Destroy all creatures."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "169"
        artist = "Adam Paquette"
        flavorText = "The tower transformed to fulfill its true purpose: as a catalyst to merge the old Multiverse with the new."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d71d250f-c0e0-44b2-877c-76f3bcab4f34.jpg?1789385887"
        inBooster = false
    }
}
