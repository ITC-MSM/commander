package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Numai Outcast
 * {3}{B}
 * Creature — Human Samurai
 * 1/1
 * Bushido 2 (Whenever this creature blocks or becomes blocked, it gets +2/+2 until end of turn.)
 * {B}, Pay 5 life: Regenerate this creature.
 *
 * The regeneration cost is a two-atom composite — mana plus a life payment — matching Assay's
 * `CostComposite(AtomMana, AtomPayLife)`. "Regenerate this creature" is [RegenerateEffect] on
 * [EffectTarget.Self]; there is no `Effects.Regenerate` facade, the effect class is the shipped
 * spelling (`m10/cards/CudgelTroll.kt`).
 *
 * **Bushido is lowered here, not handled by the engine.** [KeywordAbility.bushido] is display-only
 * vocabulary — nothing in the rules engine reads `Keyword.BUSHIDO` — so the ability it abbreviates is
 * wired explicitly, following `mh2/cards/JadeAvenger.kt`. CR 702.45a defines bushido N as one
 * triggered ability; the SDK has no single event covering "blocks or becomes blocked" from the
 * source's point of view, so it is written as two triggers over the two distinct events. They are
 * mutually exclusive in any one combat, so the pump never doubles.
 *
 * The pump targets [EffectTarget.Self] rather than `TriggeringEntity` because [Triggers.Blocks] fires
 * off a block event that does not bind the source as the triggering entity.
 */
val NumaiOutcast = card("Numai Outcast") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Samurai"
    power = 1
    toughness = 1
    oracleText = "Bushido 2 (Whenever this creature blocks or becomes blocked, it gets +2/+2 until end of turn.)\n" +
        "{B}, Pay 5 life: Regenerate this creature."

    keywordAbility(KeywordAbility.bushido(2))

    // Bushido 2, half one: "Whenever this creature blocks …"
    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Bushido 2"
    }

    // Bushido 2, half two: "… or becomes blocked, it gets +2/+2 until end of turn."
    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Bushido 2"
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.PayLife(5))
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{B}, Pay 5 life: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Adam Rex"
        flavorText = "\"Beware the blade of dishonor. It kills more silently than war, more quickly than age.\"\n—Sensei Golden-Tail"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b878d1c2-34ce-4cb4-9ea3-8d7cd2028484.jpg?1783944310"
    }
}
