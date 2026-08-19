package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Boggart Birth Rite
 * {B}
 * Kindred Sorcery — Goblin
 * Return target Goblin card from your graveyard to your hand.
 *
 * Being a Goblin card itself, it can return a copy of itself — which is why the filter matches any
 * Goblin *card*, not only Goblin creatures.
 */
val BoggartBirthRite = card("Boggart Birth Rite") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Kindred Sorcery — Goblin"
    oracleText = "Return target Goblin card from your graveyard to your hand."

    spell {
        val goblinCard = target(
            "target Goblin card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Any.withSubtype(Subtype.GOBLIN).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(goblinCard)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Ralph Horsley"
        flavorText = "Auntie excitedly held up the squalling newborn. \"This one looks like Byoog! Maybe he'll tell us what he saw and felt in the beyond.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bb4916d-ab50-45c9-bf7e-e7a7e761aef4.jpg?1783942893"
    }
}
