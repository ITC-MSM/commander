package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Herald of the Pantheon
 * {1}{G}
 * Creature — Centaur Shaman
 * 2/2
 *
 * Enchantment spells you cast cost {1} less to cast.
 * Whenever you cast an enchantment spell, you gain 1 life.
 *
 * [CostModification.ReduceGeneric] shaves only the generic part (CR 601.2f), so a mono-coloured
 * one-mana enchantment is unaffected. The life gain is a separate cast trigger — it fires whether
 * or not the reduction applied.
 */
val HeraldOfThePantheon = card("Herald of the Pantheon") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Shaman"
    oracleText = "Enchantment spells you cast cost {1} less to cast.\n" +
        "Whenever you cast an enchantment spell, you gain 1 life."
    power = 2
    toughness = 2

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Enchantment),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "180"
        artist = "Jason A. Engle"
        flavorText = "The distinction of bearing the gods' banner is nothing compared to the glory of being closer to Nyx."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffeca7a7-2a14-4166-9e89-0f4eb94b79f5.jpg"
    }
}
