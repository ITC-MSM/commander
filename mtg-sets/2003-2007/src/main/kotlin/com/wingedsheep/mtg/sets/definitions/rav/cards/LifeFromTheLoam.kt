package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val LifeFromTheLoam = card("Life from the Loam") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Return up to three target land cards from your graveyard to your hand.\nDredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)"

    keywordAbility(KeywordAbility.dredge(3))

    spell {
        target("lands", TargetObject(
            optional = true,
            count = 3,
            filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD)
        ))
        effect = Effects.Pipeline {
            val lands = gather(CardSource.ChosenTargets)
            toHand(lands)
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Terese Nielsen"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ac16d09-8bc7-407c-a757-666f4707bc90.jpg?1783943634"
    }
}
