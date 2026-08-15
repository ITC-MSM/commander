package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Kona, Rescue Beastie
 * {3}{G}
 * Legendary Creature — Beast Survivor
 * 4/3
 *
 * Survival — At the beginning of your second main phase, if Kona is tapped, you may put a
 * permanent card from your hand onto the battlefield.
 *
 * The "you may" is satisfied by [Patterns.Hand.putFromHand] choosing *up to* one card — the
 * player can decline by selecting none.
 */
val KonaRescueBeastie = card("Kona, Rescue Beastie") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Beast Survivor"
    power = 4
    toughness = 3
    oracleText = "Survival — At the beginning of your second main phase, if Kona is tapped, you " +
        "may put a permanent card from your hand onto the battlefield."

    // Survival — second main phase, if tapped: you may put a permanent card from hand onto battlefield.
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = Patterns.Hand.putFromHand(filter = GameObjectFilter.Permanent)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "187"
        artist = "Brian Valeza"
        flavorText = "She's got a heart of gold, a nose for danger, and jaws that can pulverize a " +
            "cellarspawn with a single snap."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f035294-2787-4719-8520-227bf03e84e7.jpg?1726286561"
    }
}
