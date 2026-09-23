package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The land is targeted by a reflexive trigger after the mill (CR 603.12), so a land milled by
 * this same ability can be returned.
 */
val PrimalWitchstalker = card("Primal Witchstalker") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 1
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "When this creature enters, mill four cards. When you do, return target land card from your graveyard to the battlefield tapped. " +
        "(To mill four cards, put the top four cards of your library into your graveyard.)"

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Patterns.Library.mill(4),
            optional = false,
            reflexiveEffect = Effects.PutOntoBattlefield(EffectTarget.ContextTarget(0), tapped = true),
            reflexiveTargetRequirements = listOf(
                TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Kaitlyn McCulley"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04e64af7-cca1-499e-8951-f386e84c8b5b.jpg?1789644850"
        inBooster = false
    }
}
