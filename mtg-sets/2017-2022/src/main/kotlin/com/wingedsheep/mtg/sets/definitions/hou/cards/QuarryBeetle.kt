package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Quarry Beetle
 * {4}{G}
 * Creature — Insect
 * 4/5
 * When this creature enters, you may return target land card from your graveyard to the battlefield.
 *
 * The return is [Effects.PutOntoBattlefieldFromGraveyard], the guarded move: if the land has left
 * the graveyard by the time the trigger resolves, nothing happens. It is not a land drop, so it
 * ignores the one-land-per-turn limit.
 */
val QuarryBeetle = card("Quarry Beetle") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 4
    toughness = 5
    oracleText = "When this creature enters, you may return target land card from your graveyard to the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(t)
        description = "When this creature enters, you may return target land card from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Mike Burns"
        flavorText = "\"The ruin of the past is the topsoil of the future.\" —Sokar, former Nef-crop initiate"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69e11478-bfc7-4bcc-b65c-dc2d4449e99f.jpg?1783936016"

        ruling(
            "2017-07-14",
            "Quarry Beetle's ability doesn't count as playing a land. It can return a land card to " +
                "the battlefield even if you've already played as many lands as able this turn."
        )
    }
}
