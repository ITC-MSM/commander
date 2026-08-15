package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SelectTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Disturbing Mirth
 * {B}{R}
 * Enchantment
 * When this enchantment enters, you may sacrifice another enchantment or creature. If you do,
 * draw two cards.
 * When you sacrifice this enchantment, manifest dread. (Look at the top two cards of your library.
 * Put one onto the battlefield face down as a 2/2 creature and the other into your graveyard. Turn
 * it face up any time for its mana cost if it's a creature card.)
 *
 * First ability: "sacrifice another enchantment or creature" is a resolution-time choice, not a
 * target — the player accepts the optional first, then chooses which permanent, so declining never
 * commits anything. Only when a permanent is actually sacrificed does the "If you do, draw two
 * cards" payoff run. Modelled with [ReflexiveTriggerEffect] (optional sacrifice action, untargeted
 * draw payoff) — the same shape as Treetop Sentries / Unscrupulous Contractor.
 *
 * Second ability: the standard [Triggers.Sacrificed] SELF trigger ("when you sacrifice this"),
 * reusing the shared [Patterns.Library.manifestDread] recipe (CR 701.62b).
 */
val DisturbingMirth = card("Disturbing Mirth") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, you may sacrifice another enchantment or " +
        "creature. If you do, draw two cards.\nWhen you sacrifice this enchantment, manifest " +
        "dread. (Look at the top two cards of your library. Put one onto the battlefield face " +
        "down as a 2/2 creature and the other into your graveyard. Turn it face up any time for " +
        "its mana cost if it's a creature card.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Effects.Composite(
                listOf(
                    SelectTargetEffect(
                        requirement = TargetObject(
                            filter = TargetFilter.CreatureOrEnchantment.youControl().other()
                        ),
                        storeAs = "permanentToSacrifice"
                    ),
                    Effects.SacrificeTarget(EffectTarget.PipelineTarget("permanentToSacrifice"))
                )
            ),
            optional = true,
            reflexiveEffect = Effects.DrawCards(2),
            descriptionOverride = "You may sacrifice another enchantment or creature. If you do, draw two cards."
        )
        description = "When this enchantment enters, you may sacrifice another enchantment or " +
            "creature. If you do, draw two cards."
    }

    triggeredAbility {
        trigger = Triggers.Sacrificed
        effect = Patterns.Library.manifestDread()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "212"
        artist = "Nino Vecia"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f79b0c5d-6823-439c-a011-8b9bf424bdad.jpg?1726286660"
    }
}
