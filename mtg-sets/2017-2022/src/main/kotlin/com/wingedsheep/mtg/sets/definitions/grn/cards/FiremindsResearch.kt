package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firemind's Research
 * {U}{R}
 * Enchantment
 * Whenever you cast an instant or sorcery spell, put a charge counter on this enchantment.
 * {1}{U}, Remove two charge counters from this enchantment: Draw a card.
 * {1}{R}, Remove five charge counters from this enchantment: It deals 5 damage to any target.
 */
val FiremindsResearch = card("Firemind's Research") {
    manaCost = "{U}{R}"
    colorIdentity = "RU"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast an instant or sorcery spell, put a charge counter on this enchantment.\n" +
        "{1}{U}, Remove two charge counters from this enchantment: Draw a card.\n" +
        "{1}{R}, Remove five charge counters from this enchantment: It deals 5 damage to any target."

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
    }
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{U}"),
            Costs.RemoveCounterFromSelf(Counters.CHARGE, 2)
        )
        effect = Effects.DrawCards(1)
    }
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{R}"),
            Costs.RemoveCounterFromSelf(Counters.CHARGE, 5)
        )
        val any = target("target", Targets.Any)
        effect = Effects.DealDamage(5, any)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Grzegorz Rutkowski"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4bc926b2-1e54-4c62-8b6c-fce4ef013abc.jpg?1783934133"
    }
}
