package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Konda, Lord of Eiganjo
 * {5}{W}{W}
 * Legendary Creature — Human Samurai
 * 3/3
 * Vigilance, indestructible
 * Bushido 5 (Whenever this creature blocks or becomes blocked, it gets +5/+5 until end of turn.)
 *
 * Vigilance and indestructible are engine-live keywords, so they stay a plain `keywords(…)`
 * declaration. Legendary is carried by the type line — `TypeLine.parse` reads the supertype.
 *
 * **Bushido is lowered here, not handled by the engine.** [KeywordAbility.bushido] is display-only
 * vocabulary — nothing in the rules engine reads `Keyword.BUSHIDO` — so the ability it abbreviates is
 * wired explicitly, following `mh2/cards/JadeAvenger.kt`. CR 702.45a defines bushido N as one
 * triggered ability; the SDK has no single event covering "blocks or becomes blocked" from the
 * source's point of view, so it is written as two triggers over the two distinct events. They are
 * mutually exclusive in any one combat — Konda either declares a block or is blocked, never both —
 * so the +5/+5 never doubles.
 *
 * The pump targets [EffectTarget.Self] rather than `TriggeringEntity` because [Triggers.Blocks] fires
 * off a block event that does not bind the source as the triggering entity.
 */
val KondaLordOfEiganjo = card("Konda, Lord of Eiganjo") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Samurai"
    power = 3
    toughness = 3
    oracleText = "Vigilance, indestructible\n" +
        "Bushido 5 (Whenever this creature blocks or becomes blocked, it gets +5/+5 until end of turn.)"

    keywords(Keyword.VIGILANCE, Keyword.INDESTRUCTIBLE)
    keywordAbility(KeywordAbility.bushido(5))

    // Bushido 5, half one: "Whenever this creature blocks …"
    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ModifyStats(5, 5, EffectTarget.Self)
        description = "Bushido 5"
    }

    // Bushido 5, half two: "… or becomes blocked, it gets +5/+5 until end of turn."
    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(5, 5, EffectTarget.Self)
        description = "Bushido 5"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "John Bolton"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5edab171-94b9-4e5e-ab61-bd8c6c8cfc38.jpg?1783944335"
    }
}
