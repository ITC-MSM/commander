package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.CantBeRegeneratedEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Agonizing Demise
 * {3}{B}
 * Instant
 * Kicker {1}{R}
 *
 * Destroy target nonblack creature. It can't be regenerated. If this spell was kicked,
 * Agonizing Demise deals damage equal to that creature's power to the creature's controller.
 *
 * The kicked damage is dealt before the creature is destroyed so its power (including counters and
 * continuous effects) is read while it is still on the battlefield — the engine has no last-known
 * power fallback for a spell's target once it leaves play, so off-battlefield it would read printed
 * power instead. Dealing the damage first yields the rules-correct (last-known) amount.
 */
val AgonizingDemise = card("Agonizing Demise") {
    manaCost = "{3}{B}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Kicker {1}{R} (You may pay an additional {1}{R} as you cast this spell.)\n" +
        "Destroy target nonblack creature. It can't be regenerated. If this spell was kicked, " +
        "Agonizing Demise deals damage equal to that creature's power to the creature's controller."

    keywordAbility(KeywordAbility.kicker("{1}{R}"))

    spell {
        val creature = target(
            "target nonblack creature",
            TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK))
        )
        effect = CantBeRegeneratedEffect(creature) then
            ConditionalEffect(
                condition = WasKicked,
                effect = Effects.DealDamage(DynamicAmounts.targetPower(0), EffectTarget.TargetController)
            ) then
            Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Mark Brill"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/539ac5e1-4bad-4f70-abac-e70c406bebec.jpg?1562912008"
    }
}
