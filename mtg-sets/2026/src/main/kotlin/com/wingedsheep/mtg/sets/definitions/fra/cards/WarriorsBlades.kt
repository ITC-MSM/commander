package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Warrior's Blades — Reality Fracture #163
 * {2}{R}{W} · Artifact — Equipment · Uncommon
 *
 * When this Equipment enters, it deals 3 damage to any target and you gain 3 life.
 * Equipped creature gets +2/+1.
 * Equip {3}. This ability costs {1} less to activate for each +1/+1 counter on the creature it
 * targets.
 *
 * The equip discount is the per-activation generic reduction Dragonfire Blade uses
 * (`equipAbility`'s `genericCostReduction`), fed the chosen target's +1/+1 counter count instead
 * of its color count. The reduction only touches generic mana, so it bottoms out at {0}.
 */
val WarriorsBlades = card("Warrior's Blades") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, it deals 3 damage to any target and you gain 3 life.\n" +
        "Equipped creature gets +2/+1.\n" +
        "Equip {3}. This ability costs {1} less to activate for each +1/+1 counter on the creature it targets."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, anyTarget)
            .then(Effects.GainLife(3))
    }

    staticAbility {
        ability = ModifyStats(+2, +1, Filters.EquippedCreature)
    }

    equipAbility(
        "{3}",
        genericCostReduction = DynamicAmounts.countersOnTarget(CounterTypeFilter.PlusOnePlusOne)
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Richard Kane Ferguson"
        flavorText = "Whetted against grief. Wielded with vengeance."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c63d5b0e-ee72-42ed-aa7e-484ba84507cd.jpg?1789007648"
        inBooster = false
    }
}
