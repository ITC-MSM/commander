package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Revive the Shire
 * {1}{G}
 * Sorcery
 *
 * Return target permanent card from your graveyard to your hand. Create a Food token.
 * (It's an artifact with "{2}, {T}, Sacrifice this token: You gain 3 life.")
 */
val ReviveTheShire = card("Revive the Shire") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Return target permanent card from your graveyard to your hand. Create a Food token. " +
        "(It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val t = target(
            "permanent card from your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(t) then Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Craig Elliott"
        flavorText = "In the Party Field, a beautiful young sapling leaped up: it was indeed a *mallorn*, and it was the wonder of the neighborhood."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46b7f493-1b57-4b07-8510-30703282f879.jpg?1686969568"
    }
}
