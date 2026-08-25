package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.BecomesTargetEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dream Strix — Strixhaven: School of Mages #42 (canonical printing)
 * {2}{U} · Creature — Bird Illusion · 3/2
 *
 * Flying
 * When this creature becomes the target of a spell, sacrifice it.
 * When this creature dies, learn.
 *
 * The classic Illusion drawback, rebuilt so the drawback pays you: pointing removal at it makes
 * it sacrifice itself — which *is* a death, so the second trigger fires and you Learn anyway.
 * The removal spell is then left with no legal target and is countered on resolution (CR 608.2b).
 *
 * Two rules details the wording pins down:
 * - **Spells only.** Abilities that target it do not fire the sacrifice, so it survives a tap
 *   ability or an equip. [Triggers.BecomesTargetOfSpell] is the filter-scoped ANY-bound version of
 *   this wording; Dream Strix's is self-bound, so the spec is built inline from the same
 *   `BecomesTargetEvent(spellsOnly = true)` with `TriggerBinding.SELF` rather than re-deriving a
 *   filter that means "this permanent".
 * - **Sacrifice, not destroy.** Regeneration and indestructible do not save it (CR 701.17c).
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48). The dies trigger reads last-known information,
 * so it resolves normally with the Strix already in the graveyard.
 */
val DreamStrix = card("Dream Strix") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Illusion"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature becomes the target of a spell, sacrifice it.\n" +
        "When this creature dies, learn. (You may reveal a Lesson card you own from outside the " +
        "game and put it into your hand, or discard a card to draw a card.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            event = BecomesTargetEvent(spellsOnly = true),
            binding = TriggerBinding.SELF
        )
        effect = Effects.SacrificeTarget(EffectTarget.Self)
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "YW Tang"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31d82f7a-64f1-463f-bb6b-936c3e49bf2b.jpg?1783927380"
    }
}
