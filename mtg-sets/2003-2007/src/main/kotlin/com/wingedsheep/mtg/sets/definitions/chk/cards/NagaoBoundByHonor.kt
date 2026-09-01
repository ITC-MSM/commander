package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nagao, Bound by Honor
 * {3}{W}
 * Legendary Creature — Human Samurai
 * 3/3
 * Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)
 * Whenever Nagao attacks, Samurai creatures you control get +1/+1 until end of turn.
 *
 * The attack trigger is [Triggers.Attacks] (a SELF binding — it fires only when Nagao himself is
 * declared as an attacker) over a group pump. The printed wording says "Samurai *creatures* you
 * control", so the group filter is `Creature.withSubtype(SAMURAI).youControl()` rather than a bare
 * permanent filter — the same spelling `chk/cards/CallToGlory.kt` uses for the identical phrase, and
 * the same shape Assay compiles (`ForEach` over an `IterationSpace.Group` whose body is
 * `ModifyStats` on the iterated entity). Nagao is a Samurai creature himself, so he pumps too.
 *
 * **Bushido is lowered here, not handled by the engine.** [KeywordAbility.bushido] is display-only
 * vocabulary — nothing in the rules engine reads `Keyword.BUSHIDO` — so the ability it abbreviates is
 * wired explicitly, following `mh2/cards/JadeAvenger.kt`. CR 702.45a defines bushido N as one
 * triggered ability; the SDK has no single event covering "blocks or becomes blocked" from the
 * source's point of view, so it is written as two triggers over the two distinct events. They are
 * mutually exclusive in any one combat, so the pump never doubles.
 *
 * The bushido pump targets [EffectTarget.Self] rather than `TriggeringEntity` because
 * [Triggers.Blocks] fires off a block event that does not bind the source as the triggering entity.
 */
val NagaoBoundByHonor = card("Nagao, Bound by Honor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Samurai"
    power = 3
    toughness = 3
    oracleText = "Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)\n" +
        "Whenever Nagao attacks, Samurai creatures you control get +1/+1 until end of turn."

    keywordAbility(KeywordAbility.bushido(1))

    // Bushido 1, half one: "Whenever this creature blocks …"
    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Bushido 1"
    }

    // Bushido 1, half two: "… or becomes blocked, it gets +1/+1 until end of turn."
    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Bushido 1"
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SAMURAI).youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
        description = "Whenever Nagao attacks, Samurai creatures you control get +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Dave Dorman"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a45258f4-c36d-46cc-80e8-cc458351e003.jpg?1783944334"
    }
}
