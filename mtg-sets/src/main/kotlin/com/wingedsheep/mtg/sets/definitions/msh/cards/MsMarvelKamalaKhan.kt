package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.NoMaximumHandSize
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ms. Marvel, Kamala Khan — Marvel Super Heroes #67 (rare)
 * {2}{U} · Legendary Creature — Mutant Inhuman Hero · 1/4
 *
 * Vigilance, reach
 * You have no maximum hand size.
 * Embiggen Fist — Whenever you cast a spell that targets a creature you control, draw a card.
 * Until end of turn, Ms. Marvel gains "Ms. Marvel's base power is equal to the number of cards
 * in your hand."
 *
 * Implementation notes:
 * - "You have no maximum hand size" is the printed [NoMaximumHandSize] static (Reliquary Tower /
 *   Thought Vessel shape); the cleanup step reads it off the battlefield rather than through the
 *   layer system, since maximum hand size is a player value with no Rule 613 layer.
 * - "Embiggen Fist" is an ability word (CR 207.2c) — flavor only, so it lives in the oracle text
 *   and the ability [description], not as a keyword.
 * - The trigger is the shared [Triggers.youCastSpellTargeting] facade
 *   (`SpellCastPredicate.TargetsMatching(Creature.youControl())`), the same one Mockingbird, Ace
 *   Agent uses. It fires once per qualifying spell however many of your creatures that spell
 *   targets, and Ms. Marvel herself counts as "a creature you control".
 * - The payoff draws first and *then* sets base power, matching printed order (CR 608.2): the
 *   freshly drawn card is already in hand when the power is computed.
 *
 * Known deviation — the granted quoted ability is modelled as a resolution-time Layer 7b
 * set-base-power ([Effects.SetBasePower] over [DynamicAmounts.cardsInYourHand], `EndOfTurn`)
 * rather than a genuinely granted, continuously-recomputed ability. The value is therefore locked
 * in when the trigger resolves instead of tracking later hand-size changes during the same turn
 * (each further qualifying spell re-triggers and re-sets it, so the divergence only shows after
 * the last trigger of the turn). The engine has no durational grant that participates in the layer
 * system: `GrantStaticAbilityEffect` lands in `GameState.grantedStaticAbilities`, which the
 * `StateProjector` never reads, and the only continuously-evaluated 7b floating modification
 * (`SerializableModification.SetPowerToughnessDynamic`) sets *both* power and toughness and is
 * reachable only through the animate effects. Closing this properly is an `add-feature` change
 * (a power-only dynamic set-value floating modification), not a card change.
 */
val MsMarvelKamalaKhan = card("Ms. Marvel, Kamala Khan") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Mutant Inhuman Hero"
    power = 1
    toughness = 4
    oracleText = "Vigilance, reach\n" +
        "You have no maximum hand size.\n" +
        "Embiggen Fist — Whenever you cast a spell that targets a creature you control, draw a " +
        "card. Until end of turn, Ms. Marvel gains \"Ms. Marvel's base power is equal to the " +
        "number of cards in your hand.\""

    keywords(Keyword.VIGILANCE, Keyword.REACH)

    // "You have no maximum hand size."
    staticAbility {
        ability = NoMaximumHandSize
    }

    // "Embiggen Fist — Whenever you cast a spell that targets a creature you control, draw a card.
    // Until end of turn, Ms. Marvel's base power becomes the number of cards in your hand."
    triggeredAbility {
        trigger = Triggers.youCastSpellTargeting(GameObjectFilter.Creature.youControl())
        effect = Effects.Composite(
            Effects.DrawCards(1),
            Effects.SetBasePower(
                target = EffectTarget.Self,
                power = DynamicAmounts.cardsInYourHand(),
                duration = Duration.EndOfTurn
            )
        )
        description = "Embiggen Fist — Whenever you cast a spell that targets a creature you " +
            "control, draw a card. Until end of turn, Ms. Marvel gains \"Ms. Marvel's base power " +
            "is equal to the number of cards in your hand.\""
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "67"
        artist = "Smirtouille"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9dd2d627-10fc-4045-8545-03bcf75e60ca.jpg?1783902954"
    }
}
