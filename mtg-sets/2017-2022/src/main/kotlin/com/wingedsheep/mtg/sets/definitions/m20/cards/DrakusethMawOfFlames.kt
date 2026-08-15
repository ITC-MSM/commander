package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Drakuseth, Maw of Flames
 * {4}{R}{R}{R}
 * Legendary Creature — Dragon
 * 7/7
 * Flying
 * Whenever Drakuseth attacks, it deals 4 damage to any target and 3 damage to each of up to two
 * other targets.
 *
 * Three target slots: the 4-damage target (index 0), then an "up to two other targets" slot
 * wrapped in [TargetOther] so the pair must differ both from each other (enforced within a
 * requirement by CR 601.2c) and from the 4-damage target (enforced by [TargetOther] against all
 * prior targets). `minCount = 0` makes the pair genuinely optional; an unchosen slot leaves its
 * `ContextTarget` unset and the matching [Effects.DealDamage] resolves as a no-op — the same
 * shape as Trick Shot's optional second target.
 */
val DrakusethMawOfFlames = card("Drakuseth, Maw of Flames") {
    manaCost = "{4}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dragon"
    power = 7
    toughness = 7
    oracleText = "Flying\n" +
        "Whenever Drakuseth attacks, it deals 4 damage to any target and 3 damage to each of up " +
        "to two other targets."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        target("any target", AnyTarget())
        target(
            "up to two other targets",
            TargetOther(baseRequirement = AnyTarget(count = 2, minCount = 0))
        )
        effect = Effects.Composite(
            listOf(
                Effects.DealDamage(4, EffectTarget.ContextTarget(0)),
                Effects.DealDamage(3, EffectTarget.ContextTarget(1)),
                Effects.DealDamage(3, EffectTarget.ContextTarget(2))
            )
        )
        description = "Whenever Drakuseth attacks, it deals 4 damage to any target and 3 damage " +
            "to each of up to two other targets."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "Grzegorz Rutkowski"
        flavorText = "\"Spread out, you idiots! Spread out!\"\n—Marsden, party leader, last words"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d09af78f-efde-4107-8406-cb12fd11c686.jpg?1783932980"
    }
}
