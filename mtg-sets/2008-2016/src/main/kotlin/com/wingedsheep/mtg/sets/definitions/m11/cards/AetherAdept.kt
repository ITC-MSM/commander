package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aether Adept — Magic 2011 #41
 * {1}{U}{U} · Creature — Human Wizard · 2 / 2
 *
 * When this creature enters, return target creature to its owner's hand.
 *
 * The Man-o'-War shape: a SELF-bound [Triggers.EntersBattlefield] over [Effects.ReturnToHand]. The
 * trigger is neither optional nor "up to", so it must pick a creature when one is on the
 * battlefield — including the Adept itself when it is the only legal target.
 */
val AetherAdept = card("Aether Adept") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, return target creature to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ReturnToHand(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Eric Deschamps"
        flavorText = "Some mages do their best work in solitude. Others do their best work creating it."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b551dab-1a81-406d-b708-b3b7300eb02e.jpg?1783941829"
    }
}
