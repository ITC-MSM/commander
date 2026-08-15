package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chromeshell Crab
 * {4}{U}
 * Creature — Crab Beast
 * 3/3
 * Morph {4}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 * When this creature is turned face up, you may exchange control of target creature you control
 * and target creature an opponent controls.
 */
val ChromeshellCrab = card("Chromeshell Crab") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab Beast"
    power = 3
    toughness = 3
    oracleText = "Morph {4}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen this creature is turned face up, you may exchange control of target creature you control and target creature an opponent controls."

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        optional = true
        val yours = target("creature you control", Targets.CreatureYouControl)
        val theirs = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ExchangeControl(yours, theirs)
    }

    morph = "{4}{U}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "32"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e02a40a4-fa61-4595-810a-3796e0d71507.jpg?1562940039"
        ruling("2004-10-04", "You choose the two target creatures when the triggered ability is put onto the stack. You do not have to choose Chromeshell Crab. You choose whether or not to do the exchange on resolution.")
        ruling("2004-10-04", "The exchange fails if either target is not still on the battlefield at that time.")
        ruling("2004-10-04", "The exchange fails if either you no longer control your creature or the other creature is now controlled by you.")
    }
}
