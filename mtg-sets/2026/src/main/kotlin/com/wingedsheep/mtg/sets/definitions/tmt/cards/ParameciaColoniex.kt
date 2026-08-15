package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Paramecia Coloniex
 * {1}{B}
 * Creature — Zombie Worm
 * 2/2
 *
 * When this creature enters, mill three cards.
 * When this creature dies, you may exile it. When you do, put target creature
 * card from your graveyard on top of your library.
 */
val ParameciaColoniex = card("Paramecia Coloniex") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Worm"
    oracleText = "When this creature enters, mill three cards. (Put the top three cards of your library into your graveyard.)\nWhen this creature dies, you may exile it. When you do, put target creature card from your graveyard on top of your library."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(3)
        description = "When this creature enters, mill three cards."
    }

    // "you may exile it. When you do, put target creature card from your graveyard on
    // top of your library." triggerZone = GRAVEYARD lets "it" reference Self; the
    // reflexive trigger picks its graveyard target only after the optional exile.
    triggeredAbility {
        trigger = Triggers.Dies
        triggerZone = Zone.GRAVEYARD
        effect = ReflexiveTriggerEffect(
            action = Effects.Exile(EffectTarget.Self),
            optional = true,
            reflexiveEffect = Effects.PutOnTopOfLibrary(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureCardInYourGraveyard)
        )
        description = "When this creature dies, you may exile it. When you do, put target creature card from your graveyard on top of your library."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/654e2646-78ac-4b08-bed1-3c71355d55fc.jpg?1771586897"
    }
}
