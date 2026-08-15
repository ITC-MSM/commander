package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lost Jitte
 * {1}
 * Legendary Artifact — Equipment
 *
 * Whenever equipped creature deals combat damage, put a charge counter on Lost Jitte.
 * Remove a charge counter from Lost Jitte: Choose one —
 * • Untap target land.
 * • Target creature can't block this turn.
 * • Put a +1/+1 counter on equipped creature.
 * Equip {1}
 *
 * A pared-down Umezawa's Jitte. The combat-damage trigger binds to the equipped creature
 * ([TriggerBinding.ATTACHED]) and fires on any combat damage (player or creature,
 * [RecipientFilter.Any]); it places a charge counter on the Equipment itself
 * ([EffectTarget.Self]). The activated ability has no mana cost — only the
 * remove-a-charge-counter cost — and resolves a [ModalEffect.chooseOne] over the three
 * printed modes, two of which carry their own per-mode target.
 */
val LostJitte = card("Lost Jitte") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Whenever equipped creature deals combat damage, put a charge counter on Lost Jitte.\n" +
        "Remove a charge counter from Lost Jitte: Choose one —\n" +
        "• Untap target land.\n" +
        "• Target creature can't block this turn.\n" +
        "• Put a +1/+1 counter on equipped creature.\n" +
        "Equip {1}"

    // Whenever equipped creature deals combat damage, put a charge counter on Lost Jitte.
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.Any,
            binding = TriggerBinding.ATTACHED
        )
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
    }

    // Remove a charge counter from Lost Jitte: Choose one —
    activatedAbility {
        cost = Costs.RemoveCounterFromSelf(Counters.CHARGE)
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.Untap(EffectTarget.ContextTarget(0)),
                Targets.Land,
                "Untap target land"
            ),
            Mode.withTarget(
                Effects.CantBlock(EffectTarget.ContextTarget(0)),
                Targets.Creature,
                "Target creature can't block this turn"
            ),
            Mode.noTarget(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.EquippedCreature),
                "Put a +1/+1 counter on equipped creature"
            ),
            countsAsModalSpell = false
        )
        description = "Remove a charge counter from Lost Jitte: Choose one — Untap target land; Target creature can't block this turn; or Put a +1/+1 counter on equipped creature."
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "23"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c936504c-4e90-408f-ba98-0fb8c0378471.jpg?1739804226"
    }
}
