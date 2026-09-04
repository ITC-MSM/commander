package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Nahiri's Machinations (Shadows over Innistrad #28)
 * {1}{W}
 * Enchantment
 *
 * At the beginning of combat on your turn, target creature you control gains indestructible until end of turn.
 * {1}{R}: This enchantment deals 1 damage to target blocking creature.
 *
 * "On your turn" is [Triggers.BeginCombat]'s own scope, and "target blocking creature" is
 * [TargetFilter.BlockingCreature] — the blocking check is read live from combat state, so the
 * ability is only activatable once blockers are declared.
 */
val NahirisMachinations = card("Nahiri's Machinations") {
    manaCost = "{1}{W}"
    colorIdentity = "RW"
    typeLine = "Enchantment"
    oracleText = "At the beginning of combat on your turn, target creature you control gains indestructible until end of turn.\n" +
        "{1}{R}: This enchantment deals 1 damage to target blocking creature."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("target", TargetCreature(filter = TargetFilter.CreatureYouControl))
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        val t = target("target", TargetCreature(filter = TargetFilter.BlockingCreature))
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Lake Hurwitz"
        flavorText = "\"Sorin, I'm going to take everything from you.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9ba0f1c-8641-4f5a-8f2e-f969fc7a058a.jpg?1783937817"
    }
}
