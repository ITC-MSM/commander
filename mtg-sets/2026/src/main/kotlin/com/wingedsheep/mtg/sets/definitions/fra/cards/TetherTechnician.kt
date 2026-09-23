package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val TetherTechnician = card("Tether Technician") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Artificer"
    power = 4
    toughness = 5
    oracleText = "Reach\n" +
        "When this creature enters, you may discard a card. When you do, this creature deals 2 damage to any target."

    keywords(Keyword.REACH)

    // "you may discard a card. When you do, ..." — a reflexive trigger; the target is chosen
    // only once the discard has happened.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Effects.Discard(1),
            optional = true,
            reflexiveEffect = Effects.DealDamage(2, EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.Any)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Victor Maury"
        flavorText = "Konstrari cadets are Hexhaven's front line against any threat, be it Eradian abominations or extradimensional academies."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b75bbf46-a421-467a-9433-6cf22398a3a5.jpg?1789556839"
        inBooster = false
    }
}
