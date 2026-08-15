package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CantBlockEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ty Lee, Artful Acrobat
 * {2}{R}
 * Legendary Creature — Human Performer
 * 3/2
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Whenever Ty Lee attacks, you may pay {1}. When you do, target creature can't block this turn.
 *
 * The attack trigger is a "When you do" reflexive: the optional {1} payment is the action, and
 * the reflexive ability — which targets a creature, chosen as it goes on the stack — only fires
 * if the payment is made.
 */
val TyLeeArtfulAcrobat = card("Ty Lee, Artful Acrobat") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Performer"
    power = 3
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Whenever Ty Lee attacks, you may pay {1}. When you do, target creature can't block this turn."

    prowess()

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = ReflexiveTriggerEffect(
            // "you may pay {1}"
            action = PayManaCostEffect(ManaCost.parse("{1}")),
            optional = true,
            // "When you do, target creature can't block this turn."
            reflexiveEffect = CantBlockEffect(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.Creature)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Rose Benjamin"
        flavorText = "The circus called to Ty Lee . . . until Azula called a little louder."
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcd9df24-272b-4aa1-b05f-6ee6b3d3dfe7.jpg?1764121084"
    }
}
