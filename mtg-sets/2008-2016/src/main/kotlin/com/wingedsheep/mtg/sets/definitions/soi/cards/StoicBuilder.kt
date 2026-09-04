package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Stoic Builder (Shadows over Innistrad #231)
 * {2}{G}
 * Creature — Human
 * 2 / 3
 *
 * When this creature enters, you may return target land card from your graveyard to your hand.
 */
val StoicBuilder = card("Stoic Builder") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, you may return target land card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Howard Lyon"
        flavorText = "Some have returned to Hollowhenge, the ruins of Avabruck, to restore the lost capital of Kessig."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0820f17c-ab4a-4a14-84ff-4ea200bef112.jpg?1783937720"
    }
}
