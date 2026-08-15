package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skewer Slinger
 * {1}{R}
 * Creature — Dwarf Knight
 * 1/3
 *
 * Reach
 * Whenever this creature blocks or becomes blocked by a creature, this creature deals 1 damage to that creature.
 *
 * The single trigger covers both halves of "blocks or becomes blocked": [Triggers.BlocksOrBecomesBlockedBy]
 * exposes the combat partner (the blocked attacker, or the creature blocking it) as
 * [EffectTarget.TriggeringEntity]. "That creature" is not a target — it's the combat partner.
 */
val SkewerSlinger = card("Skewer Slinger") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Knight"
    power = 1
    toughness = 3
    oracleText = "Reach\nWhenever this creature blocks or becomes blocked by a creature, this creature deals 1 damage to that creature."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.BlocksOrBecomesBlockedBy(Filters.Creature)
        effect = Effects.DealDamage(1, EffectTarget.TriggeringEntity, damageSource = EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e78e50ca-d27b-45db-91fa-7fef3cad16d0.jpg?1783915089"
    }
}
