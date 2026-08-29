package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Surgespanner
 * {2}{U}{U}
 * Creature — Merfolk Wizard
 * 2/2
 * Whenever this creature becomes tapped, you may pay {1}{U}. If you do, return target permanent to
 * its owner's hand.
 *
 * "You may pay {1}{U}. If you do, …" is an optional cost rider on the triggered ability itself
 * ([MayPayManaEffect] → `Gate.MayPay`), not a reflexive trigger — the payment and the bounce both
 * happen as this one ability resolves. Customs Depot is the same shape.
 *
 * The target is chosen when the ability goes on the stack, before the payment is offered, which is
 * why `target` sits on the ability rather than inside the gate.
 *
 * Rulings (2007-10-01): this is a triggered ability, not an activated one — tapping has to come
 * from somewhere else (attacking, an effect). It only fires on an actual untapped → tapped change,
 * so an already-tapped Surgespanner that something tries to tap again does not trigger.
 */
val Surgespanner = card("Surgespanner") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature becomes tapped, you may pay {1}{U}. If you do, return " +
        "target permanent to its owner's hand."

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        val permanent = target("target permanent", Targets.Permanent)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}{U}"),
            effect = Effects.ReturnToHand(permanent),
        )
        description = "you may pay {1}{U}. If you do, return target permanent to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "Warren Mahy"
        flavorText = "They ride on waves of Æther, washing out anything that might pollute the Merrow Lanes."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d5b00cb-ebc3-44e8-970d-724150eb7876.jpg?1783942896"
        ruling("2007-10-01", "This is a triggered ability, not an activated ability. It doesn't allow you to tap the creature whenever you want; rather, you need some other way of tapping it, such as by attacking with the creature.")
        ruling("2007-10-01", "For the ability to trigger, the creature has to actually change from untapped to tapped. If an effect attempts to tap the creature, but it was already tapped at the time, this ability won't trigger.")
    }
}
