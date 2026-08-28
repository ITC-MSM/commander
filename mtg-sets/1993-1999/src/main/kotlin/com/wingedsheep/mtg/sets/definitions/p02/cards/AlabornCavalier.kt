package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Alaborn Cavalier
 * {2}{W}{W}
 * Creature — Human Knight
 *
 * The Portal "tapper on attack" shape — Seasoned Marshal in Portal, Flanking Troops in
 * Portal Three Kingdoms. The optional clause is [MayEffect] around the tap, so the gate is the
 * consent and the tap is the whole of the then-branch.
 */
val AlabornCavalier = card("Alaborn Cavalier") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Whenever this creature attacks, you may tap target creature."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("target", Targets.Creature)
        effect = MayEffect(Effects.Tap(creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Kev Walker"
        flavorText = "\"Course he ran! *I* wouldn't want to stare down that barrel, either!\"\n—Alaborn soldier"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e865658-67c8-43b2-8d3a-909fb1c17e8a.jpg"
    }
}
