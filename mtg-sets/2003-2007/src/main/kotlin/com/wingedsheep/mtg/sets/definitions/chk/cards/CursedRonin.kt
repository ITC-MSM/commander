package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cursed Ronin
 * {3}{B}
 * Creature — Human Samurai
 * 1/1
 * Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)
 * {B}: This creature gets +1/+1 until end of turn.
 *
 * The firebreathing-shaped pump is a plain activated ability: a mana-only cost and
 * `ModifyStats(+1/+1)` on [EffectTarget.Self], matching Assay's compiled model exactly. It is
 * repeatable, so no once-per-turn restriction is declared.
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
val CursedRonin = card("Cursed Ronin") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Samurai"
    power = 1
    toughness = 1
    oracleText = "Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)\n" +
        "{B}: This creature gets +1/+1 until end of turn."

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

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "{B}: This creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Carl Critchlow"
        flavorText = "\"You are fortunate, my enemy. You have paid the price but once. I never stop paying.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8f24fe9-22c4-4e53-9d7a-3cbf5533ac9b.jpg?1783944316"
    }
}
