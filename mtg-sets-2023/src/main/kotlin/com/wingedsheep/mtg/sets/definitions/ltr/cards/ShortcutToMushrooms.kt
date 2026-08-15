package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shortcut to Mushrooms
 * {1}{G}
 * Enchantment
 *
 * When this enchantment enters, the Ring tempts you.
 * At the beginning of your end step, if a permanent you controlled left the battlefield
 * this turn, put a +1/+1 counter on target creature you control.
 *
 * The end-step trigger is gated by `Conditions.YouHadPermanentLeaveBattlefieldThisTurn`
 * (LTR Gap 19) — a per-player tracker incremented by `ZoneTransitionService` whenever a
 * permanent you controlled left the battlefield this turn (creatures, lands, tokens,
 * anything), and cleared at end-of-turn cleanup.
 *
 * Intervening-if gating (Rule 603.4): the condition is evaluated both at trigger time
 * and at resolution. If the player loses their last creature between the trigger firing
 * and resolution, the trigger still resolves — the *permanent-left-battlefield* state is
 * a turn flag, not a board census.
 */
val ShortcutToMushrooms = card("Shortcut to Mushrooms") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, the Ring tempts you.\n" +
        "At the beginning of your end step, if a permanent you controlled left the " +
        "battlefield this turn, put a +1/+1 counter on target creature you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "187"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13bc14f9-c90c-499d-9024-3182d78e0a88.jpg?1686969589"
    }
}
