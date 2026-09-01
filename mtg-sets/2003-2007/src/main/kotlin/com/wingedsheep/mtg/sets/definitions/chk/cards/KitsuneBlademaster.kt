package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kitsune Blademaster
 * {2}{W}
 * Creature — Fox Samurai
 * 2/2
 * First strike
 * Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)
 *
 * First strike is an engine-live keyword, so it stays a plain `keywords(…)` declaration.
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
val KitsuneBlademaster = card("Kitsune Blademaster") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Fox Samurai"
    power = 2
    toughness = 2
    oracleText = "First strike\n" +
        "Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)"

    keywords(Keyword.FIRST_STRIKE)
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

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Keith Garletts"
        flavorText = "Those kitsune trained in the blade preferred to fight with a blade-catching jitte in the off hand, buying them just enough time to deliver the first deadly cut."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb9d108d-ee19-4b1d-9d4b-b4c4d9b8ad0d.jpg?1783944336"
    }
}
