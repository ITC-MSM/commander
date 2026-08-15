package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Mishra's Self-Replicator
 * {5}
 * Artifact Creature — Assembly-Worker
 * 2/2
 * Whenever you cast a historic spell, you may pay {1}. If you do, create a token
 * that's a copy of Mishra's Self-Replicator. (Artifacts, legendaries, and Sagas are historic.)
 */
val MishrasSelfReplicator = card("Mishra's Self-Replicator") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Assembly-Worker"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast a historic spell, you may pay {1}. If you do, create a token that's a copy of Mishra's Self-Replicator. (Artifacts, legendaries, and Sagas are historic.)"

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.CreateTokenCopyOfSelf()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "223"
        artist = "Joseph Meehan"
        flavorText = "It has witnessed history's most significant events, one incarnation after another."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6f342b2-1ba6-4b72-9f03-bc076c795b3d.jpg?1562744634"
        ruling("2018-04-27", "The token will have Mishra's Self-Replicator's ability. It will also be able to create copies of itself.")
        ruling("2018-04-27", "The token won't copy counters or damage marked on Mishra's Self-Replicator, nor will it copy other effects that have changed its characteristics.")
        ruling("2018-04-27", "If Mishra's Self-Replicator leaves the battlefield before its triggered ability resolves, the token will still enter the battlefield as a copy of Mishra's Self-Replicator, using its last known characteristics.")
    }
}
