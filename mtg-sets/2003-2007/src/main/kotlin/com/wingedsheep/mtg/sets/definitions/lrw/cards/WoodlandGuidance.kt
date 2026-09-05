package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val WoodlandGuidance = card("Woodland Guidance") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Return target card from your graveyard to your hand. Clash with an opponent. If you win, untap all Forests you control. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)\nExile Woodland Guidance."

    spell {
        val card = target(
            "target card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Any.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(card).then(
            Patterns.Mechanic.clash(
                Patterns.Group.untapGroup(
                    GroupFilter(GameObjectFilter.Land.withSubtype(Subtype.FOREST).youControl())
                )
            )
        )
        selfExile()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "243"
        artist = "Richard Sardinha"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92adead0-f8b4-4c49-b4ce-d9253980ee03.jpg?1783942855"
    }
}
