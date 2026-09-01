package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Samurai Enforcers
 * {4}{W}{W}
 * Creature — Human Samurai
 * 4/4
 * Bushido 2 (Whenever this creature blocks or becomes blocked, it gets +2/+2 until end of turn.)
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
val SamuraiEnforcers = card("Samurai Enforcers") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Samurai"
    power = 4
    toughness = 4
    oracleText = "Bushido 2 (Whenever this creature blocks or becomes blocked, it gets +2/+2 until end of turn.)"

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

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Mitch Cotie"
        flavorText = "From the moment they swore their oaths, they belonged to their lord, sword and soul."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b2be3fe-87a2-47b2-8af1-b99a48622c7b.jpg?1783944332"
    }
}
