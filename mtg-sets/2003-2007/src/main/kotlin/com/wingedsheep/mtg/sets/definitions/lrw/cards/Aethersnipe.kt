package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aethersnipe
 * {5}{U}
 * Creature — Elemental
 * 4/4
 * When this creature enters, return target nonland permanent to its owner's hand.
 * Evoke {1}{U}{U}
 *
 * The enters trigger is what evoke buys: evoking still puts the creature onto the battlefield, so
 * the bounce happens and the body is then sacrificed. `evoke` is the card DSL's alt-cost field —
 * the engine offers the second cast option and schedules the sacrifice itself.
 */
val Aethersnipe = card("Aethersnipe") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, return target nonland permanent to its owner's hand.\n" +
        "Evoke {1}{U}{U} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    evoke = "{1}{U}{U}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(permanent)
        description = "return target nonland permanent to its owner's hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c44c623e-9ae1-495d-b72d-166f2ed4cf2c.jpg?1783942906"
    }
}
