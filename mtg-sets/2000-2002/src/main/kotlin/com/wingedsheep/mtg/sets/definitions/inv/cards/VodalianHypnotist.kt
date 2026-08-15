package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Vodalian Hypnotist
 * {1}{U}
 * Creature — Merfolk Wizard
 * 1/1
 * {2}{B}, {T}: Target player discards a card. Activate only as a sorcery.
 */
val VodalianHypnotist = card("Vodalian Hypnotist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "{2}{B}, {T}: Target player discards a card. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        val targetPlayer = target("target player", TargetPlayer())
        effect = Effects.Discard(1, targetPlayer)
        description = "{2}{B}, {T}: Target player discards a card. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Rebecca Guay"
        flavorText = "\"Deceit is the heart of war.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/721fd877-0a28-4002-8b47-058bac4ac44d.jpg?1562917815"
    }
}
