package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sanctum Gargoyle
 * {3}{W}
 * Artifact Creature — Gargoyle
 * 2/3
 * Flying
 * When this creature enters, you may return target artifact card from your graveyard to your hand.
 */
val SanctumGargoyle = card("Sanctum Gargoyle") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Gargoyle"
    power = 2
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, you may return target artifact card from your graveyard to your hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target(
            "target",
            TargetObject(filter = TargetFilter.ArtifactInYourGraveyard)
        )
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Shelly Wan"
        flavorText = "As their supplies of etherium dwindled, mechanists sent gargoyles farther and farther afield in search of salvage."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1da1a2d5-d1fe-4aa2-aa83-64d6c269f7bc.jpg?1783942579"

        ruling("2008-10-01", "The only difference between a colored artifact and a colorless artifact is, obviously, its color. Unlike most artifacts, a colored artifact requires colored mana to cast. Also unlike most artifacts, a colored artifact has a color in all zones. It will interact with cards that care about color. Other than that, a colored artifact behaves just like any other artifact. It will interact as normal with any card that cares about artifacts, such as Shatter or Arcbound Ravager.")
    }
}
