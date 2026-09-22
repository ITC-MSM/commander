package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val RestoreWithEmpathy = card("Restore with Empathy") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Return target permanent card from your graveyard to your hand. You gain 4 life."

    spell {
        val permanent = target("permanent", TargetObject(
            filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD)
        ))
        effect = Effects.ReturnToHand(permanent) then Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "Eelis Kyttanen"
        flavorText = "Garruk looked at the young, scared boy from Vryn. \"I have also begun anew,\" he rumbled."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/3546b93b-a7d1-451d-a369-22cc8ddcd00d.jpg?1788865848"
        inBooster = false
    }
}
