package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModeOption
import com.wingedsheep.sdk.scripting.conditions.SourceChosenModeIs

/**
 * Hollowmurk Siege
 * {B}{G}
 * Enchantment
 *
 * As this enchantment enters, choose Sultai or Abzan.
 * • Sultai — Whenever a counter is put on a creature you control, draw a card.
 *   This ability triggers only once each turn.
 * • Abzan — Whenever you attack, put a +1/+1 counter on target attacking creature.
 *   It gains menace until end of turn.
 *
 * Implementation: the cast-time choice is recorded via [EntersWithChoice] (ChoiceType.MODE).
 * Both modes are triggered abilities gated by [SourceChosenModeIs]. The Sultai trigger uses
 * [Triggers.countersPlacedOn] (any counter type, on a creature you control, not restricted to
 * a per-creature first time) plus `oncePerTurn = true` for the "triggers only once each turn"
 * clause.
 */
val HollowmurkSiege = card("Hollowmurk Siege") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "As this enchantment enters, choose Sultai or Abzan.\n" +
        "• Sultai — Whenever a counter is put on a creature you control, draw a card. This ability triggers only once each turn.\n" +
        "• Abzan — Whenever you attack, put a +1/+1 counter on target attacking creature. It gains menace until end of turn."

    replacementEffect(
        EntersWithChoice(
            choiceType = ChoiceType.MODE,
            modeOptions = listOf(
                ModeOption(
                    id = "sultai",
                    label = "Sultai",
                    description = "Whenever a counter is put on a creature you control, draw a card. Once each turn.",
                    iconKey = "sultai"
                ),
                ModeOption(
                    id = "abzan",
                    label = "Abzan",
                    description = "Whenever you attack, put a +1/+1 counter on target attacking creature. It gains menace.",
                    iconKey = "abzan"
                )
            )
        )
    )

    // Sultai — Whenever a counter is put on a creature you control, draw a card. Once each turn.
    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl(),
            counterType = Counters.ANY,
            firstTimeEachTurn = false,
        )
        triggerCondition = SourceChosenModeIs("sultai")
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    // Abzan — Whenever you attack, put a +1/+1 counter on target attacking creature.
    // It gains menace until end of turn.
    triggeredAbility {
        trigger = Triggers.YouAttack
        triggerCondition = SourceChosenModeIs("abzan")
        val attacker = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, attacker)
            .then(Effects.GrantKeyword(Keyword.MENACE, attacker))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ac0e136-8877-4bfc-a831-2bf7b7b5ad1e.jpg?1743204751"
        ruling("2025-04-04", "If you somehow control Hollowmurk Siege and no choice was made for it (perhaps because another permanent on the battlefield became a copy of it), it has neither of the two abilities.")
        ruling("2025-04-04", "An ability that triggers when counters are put on a permanent will trigger if that permanent somehow enters the battlefield with those counters.")
    }
}
