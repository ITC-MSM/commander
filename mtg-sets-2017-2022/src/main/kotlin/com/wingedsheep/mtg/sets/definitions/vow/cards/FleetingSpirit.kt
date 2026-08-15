package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fleeting Spirit — Innistrad: Crimson Vow #14
 * {1}{W} · Creature — Spirit · Uncommon · 3/1
 * Artist: Evyn Fong
 *
 * {W}, Exile three cards from your graveyard: This creature gains first strike until end of turn.
 * Discard a card: Exile this creature. Return it to the battlefield under its owner's control at the
 * beginning of the next end step.
 *
 * Ability 1 is a straightforward pump: the additional cost exiles three graveyard cards
 * ([Costs.ExileFromGraveyard]) and it grants first strike to itself for the turn
 * ([Effects.GrantKeyword] over [EffectTarget.Self]). Ability 2 is a self-blink for protection
 * ("discard a card" additional cost) — the Meandering Towershell / Skyskipper Duo shape, applied to
 * the source: move Fleeting Spirit to exile now, and a delayed trigger
 * ([CreateDelayedTriggerEffect] at [Step.END]) returns it to the battlefield at the beginning of the
 * next end step. Because it re-enters as a new object, any auras/counters fall off and its ETB
 * timestamp resets, matching the oracle's "return it."
 */
val FleetingSpirit = card("Fleeting Spirit") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 1
    oracleText = "{W}, Exile three cards from your graveyard: This creature gains first strike " +
        "until end of turn.\n" +
        "Discard a card: Exile this creature. Return it to the battlefield under its owner's " +
        "control at the beginning of the next end step."

    // {W}, Exile three cards from your graveyard: This creature gains first strike until end of turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.ExileFromGraveyard(3))
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
        description = "{W}, Exile three cards from your graveyard: This creature gains first strike " +
            "until end of turn."
    }

    // Discard a card: Exile this creature. Return it at the beginning of the next end step.
    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.Composite(
            Effects.Move(EffectTarget.Self, Zone.EXILE),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD)
            )
        )
        description = "Discard a card: Exile this creature. Return it to the battlefield under its " +
            "owner's control at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Evyn Fong"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db9d80f7-9742-4437-a9f4-6717a678f935.jpg?1783924922"
    }
}
