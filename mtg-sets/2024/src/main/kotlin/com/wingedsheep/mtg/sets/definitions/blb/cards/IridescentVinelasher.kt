package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Iridescent Vinelasher
 * {B}
 * Creature — Lizard Assassin
 * 1/2
 *
 * Offspring {2} (You may pay an additional {2} as you cast this spell.
 * If you do, when this creature enters, create a 1/1 token copy of it.)
 *
 * Landfall — Whenever a land you control enters, this creature deals
 * 1 damage to target opponent.
 */
val IridescentVinelasher = card("Iridescent Vinelasher") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Lizard Assassin"
    power = 1
    toughness = 2
    oracleText = "Offspring {2} (You may pay an additional {2} as you cast this spell. If you do, when this creature enters, create a 1/1 token copy of it.)\nLandfall — Whenever a land you control enters, this creature deals 1 damage to target opponent."

    // Offspring: modeled as kicker-like additional cost
    keywordAbility(KeywordAbility.offspring("{2}"))

    // Offspring ETB: when this enters, if offspring was paid, create a 1/1 token copy
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasKicked
        effect = Effects.CreateTokenCopyOfSelf(overridePower = 1, overrideToughness = 1)
    }

    // Landfall: whenever a land you control enters, deal 1 damage to target opponent
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        val opponent = target("opponent", Targets.Opponent)
        effect = Effects.DealDamage(1, opponent)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "99"
        artist = "Aaron Miller"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2bc854c-4e72-48e0-a098-e3451d6e511d.jpg?1721426442"
    }
}
