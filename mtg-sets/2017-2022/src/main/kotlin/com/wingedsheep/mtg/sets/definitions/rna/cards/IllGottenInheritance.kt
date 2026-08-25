package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ill-Gotten Inheritance — Ravnica Allegiance #77
 * {3}{B} · Enchantment
 *
 * The upkeep drain hits every opponent at once ([Player.EachOpponent]) while the life gain is
 * flat; the sacrifice ability targets a single opponent instead. Both halves are one composite
 * per ability, so damage and life gain resolve together.
 */
val IllGottenInheritance = card("Ill-Gotten Inheritance") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, this enchantment deals 1 damage to each opponent and you gain 1 life.\n" +
        "{5}{B}, Sacrifice this enchantment: It deals 4 damage to target opponent and you gain 4 life."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(listOf(
            Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        ))
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}{B}"), Costs.SacrificeSelf)
        val victim = target("target", Targets.Opponent)
        effect = Effects.Composite(listOf(
            Effects.DealDamage(4, victim),
            Effects.GainLife(4)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Winona Nelson"
        flavorText = "\"The suffering of others is not my concern.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d44b342-f611-4836-a9d5-83b00a24318f.jpg"
    }
}
