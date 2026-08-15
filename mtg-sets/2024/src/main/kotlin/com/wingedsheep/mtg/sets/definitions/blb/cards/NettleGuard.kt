package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nettle Guard
 * {1}{W}
 * Creature — Mouse Soldier
 * 3/1
 *
 * Valiant — Whenever this creature becomes the target of a spell or ability
 * you control for the first time each turn, it gets +0/+2 until end of turn.
 *
 * {1}, Sacrifice this creature: Destroy target artifact or enchantment.
 */
val NettleGuard = card("Nettle Guard") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mouse Soldier"
    power = 3
    toughness = 1
    oracleText = "Valiant — Whenever this creature becomes the target of a spell or ability you control for the first time each turn, it gets +0/+2 until end of turn.\n{1}, Sacrifice this creature: Destroy target artifact or enchantment."

    // Valiant: +0/+2 until end of turn
    triggeredAbility {
        trigger = Triggers.Valiant
        effect = Effects.ModifyStats(0, 2, EffectTarget.Self)
    }

    // {1}, Sacrifice: Destroy target artifact or enchantment
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        val t = target("artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Rob Rey"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c9c3cc3-2aa2-453e-a17c-2baeeaabe0a9.jpg?1721425890"
    }
}
