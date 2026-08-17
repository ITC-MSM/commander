package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * High Priest of Penance
 * {W}{B}
 * Creature — Human Cleric
 * 1/1
 * Whenever this creature is dealt damage, you may destroy target nonland permanent.
 *
 * A 1/1 that punishes any answer aimed at it: [Triggers.TakesDamage] is the SELF-bound incoming-damage
 * event, so it fires even on lethal damage (the trigger is detected off the damage event before
 * state-based actions bury the Priest), and "you may" is the [MayEffect] wrapper around
 * [Effects.Destroy].
 */
val HighPriestOfPenance = card("High Priest of Penance") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature is dealt damage, you may destroy target nonland permanent."

    triggeredAbility {
        trigger = Triggers.TakesDamage
        val t = target(
            "target nonland permanent",
            TargetPermanent(filter = TargetFilter.NonlandPermanent),
        )
        effect = MayEffect(Effects.Destroy(t))
        description = "Whenever this creature is dealt damage, you may destroy target nonland permanent."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Mark Zug"
        flavorText = "\"All I require is faith, loyalty, obedience, trust, and complete and utter devotion.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84a3ff8d-6d7e-49f0-8d30-7f8c23db568b.jpg?1783940106"
    }
}
