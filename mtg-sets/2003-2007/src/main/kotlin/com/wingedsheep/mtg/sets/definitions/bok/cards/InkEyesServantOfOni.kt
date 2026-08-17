package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ink-Eyes, Servant of Oni
 * {4}{B}{B}
 * Legendary Creature — Rat Ninja
 * 5/4
 *
 * Ninjutsu {3}{B}{B}
 * Whenever Ink-Eyes deals combat damage to a player, you may put target creature card from
 * that player's graveyard onto the battlefield under your control.
 * {1}{B}: Regenerate Ink-Eyes.
 */
val InkEyesServantOfOni = card("Ink-Eyes, Servant of Oni") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Rat Ninja"
    oracleText = "Ninjutsu {3}{B}{B} ({3}{B}{B}, Return an unblocked attacker you control to hand: " +
        "Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever Ink-Eyes deals combat damage to a player, you may put target creature card from " +
        "that player's graveyard onto the battlefield under your control.\n" +
        "{1}{B}: Regenerate Ink-Eyes."
    power = 5
    toughness = 4

    ninjutsu("{3}{B}{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val t = target("target", TargetObject(filter = TargetFilter.CreatureInGraveyard.ownedByOpponent()))
        effect = MayEffect(Effects.PutOntoBattlefieldUnderYourControl(t))
    }

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/386d391d-a3f8-49a4-802a-409944b2acf3.jpg?1783944198"
    }
}
