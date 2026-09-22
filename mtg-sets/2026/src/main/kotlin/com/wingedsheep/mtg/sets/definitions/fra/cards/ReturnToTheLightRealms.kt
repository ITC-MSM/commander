package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource

val ReturnToTheLightRealms = card("Return to the Light Realms") {
    manaCost = "{7}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return all nonland permanent cards from your graveyard to the battlefield."

    spell {
        effect = Effects.Pipeline {
            val permanents = gather(CardSource.FromZone(Zone.GRAVEYARD, filter = GameObjectFilter.NonlandPermanent))
            move(permanents, CardDestination.ToZone(Zone.BATTLEFIELD))
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "20"
        artist = "Anna Steinbauer"
        flavorText = "\"For every living person there are generations of souls reaching out to be remembered.\"\n—Liliana the Faultless"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e72f397-2384-40f1-882b-f627664d97df.jpg?1788878107"
        inBooster = false
    }
}
