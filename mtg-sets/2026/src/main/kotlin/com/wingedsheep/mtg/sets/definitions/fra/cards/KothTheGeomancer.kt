package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val KothTheGeomancer = card("Koth, the Geomancer") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "Reach\n" +
        "Landfall — Whenever a land you control enters, Koth deals 1 damage to each opponent. If that land is a Mountain, add {R}."
    power = 3
    toughness = 2

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.Composite(
            Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            ConditionalEffect(
                condition = Conditions.EntityMatches(EffectTarget.TriggeringEntity, Filters.MountainCard),
                effect = Effects.AddMana(Color.RED)
            )
        )
        description = "Landfall — Whenever a land you control enters, Koth deals 1 damage to each opponent. If that land is a Mountain, add {R}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "248"
        artist = "Kieran Yanner"
        flavorText = "\"My home was lost a long time ago. But maybe we can forge something new.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54f64e95-5a97-4d7c-9939-7f33a3165562.jpg?1789470892"
        inBooster = false
    }
}
