package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Devoted Retainer
 * {W}
 * Creature — Human Samurai
 * 1/1
 * Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)
 *
 * **Bushido is lowered here, not handled by the engine.** [KeywordAbility.bushido] is display-only
 * vocabulary — nothing in the rules engine reads `Keyword.BUSHIDO` — so the ability it abbreviates is
 * wired explicitly, following `mh2/cards/JadeAvenger.kt`. CR 702.45a defines bushido N as one
 * triggered ability, "Whenever this creature blocks or becomes blocked, it gets +N/+N until end of
 * turn"; the SDK has no single event covering both directions from the source's point of view, so it
 * is written as two triggers over the two distinct events. They are mutually exclusive in any one
 * combat — the Retainer either declares a block or is blocked, never both — so the pump never doubles.
 *
 * The pump targets [EffectTarget.Self] rather than `TriggeringEntity` because [Triggers.Blocks] fires
 * off a block event that does not bind the source as the triggering entity.
 */
val DevotedRetainer = card("Devoted Retainer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Samurai"
    power = 1
    toughness = 1
    oracleText = "Bushido 1 (Whenever this creature blocks or becomes blocked, it gets +1/+1 until end of turn.)"

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
        collectorNumber = "7"
        artist = "Greg Hildebrandt"
        flavorText = "Deep within Eiganjo Castle lay the Palace of Infinite Halls, a seemingly endless network of corridors once guarded by a seemingly endless legion of samurai."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc41d6d6-d7e5-4874-b6e2-fa4c72454f15.jpg?1783944342"
    }
}
