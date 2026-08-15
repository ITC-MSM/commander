package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Miasma Demon — Duskmourn: House of Horror #109
 * {4}{B}{B} · Creature — Demon · 5/4
 *
 * Flying
 * When this creature enters, you may discard any number of cards. When you do, up to that many
 * target creatures each get -2/-2 until end of turn.
 *
 * Modeled as a [ReflexiveTriggerEffect]: the action is "discard any number of cards"
 * ([Patterns.Hand.discardAnyNumber], which stores the discarded set so its size is readable as
 * `discarded_count`), and the reflexive payoff selects up to that many target creatures —
 * [TargetCreature.dynamicMaxCount] = `DynamicAmount.VariableReference("discarded_count")`, resolved
 * against the resolving ability's pipeline when the reflexive targets are chosen (after the
 * discard). [ForEachTargetEffect] applies -2/-2 until end of turn to each chosen creature.
 *
 * Per CR 603.10c / the oracle "when you do" reflexive shape, the payoff fires off the discard
 * action itself; discarding zero cards means there is no "that many", so the reflexive trigger
 * targets nothing — handled by `optional = false` on the reflexive wrapper (the action is the
 * "may", a discard of zero is a legal no-op) combined with the 0-cap on targets.
 */
val MiasmaDemon = card("Miasma Demon") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, you may discard any number of cards. When you do, up to that " +
        "many target creatures each get -2/-2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Patterns.Hand.discardAnyNumber(),
            optional = false,
            reflexiveEffect = ForEachTargetEffect(
                listOf(
                    Effects.ModifyStats(-2, -2, EffectTarget.ContextTarget(0))
                )
            ),
            reflexiveTargetRequirements = listOf(
                TargetCreature(
                    optional = true,
                    dynamicMaxCount = DynamicAmount.VariableReference("discarded_count")
                )
            ),
            descriptionOverride = "You may discard any number of cards. When you do, up to that " +
                "many target creatures each get -2/-2 until end of turn."
        )
        description = "When this creature enters, you may discard any number of cards. When you " +
            "do, up to that many target creatures each get -2/-2 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Mathias Kollros"
        flavorText = "\"Hush, little ants. I can rid you of your troubles with a breath. All I " +
            "ask in return is a drop of blood... and a promise.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d167c00-75ff-4301-855a-8319b89e3689.jpg?1726286255"
    }
}
