package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

val Brightflame = card("Brightflame") {
    manaCost = "{X}{R}{R}{W}{W}"
    colorIdentity = "RW"
    typeLine = "Sorcery"
    oracleText = "Radiance — Brightflame deals X damage to target creature and each other creature that shares a color with it. You gain life equal to the damage dealt this way."

    spell {
        val victim = target("target creature", Targets.Creature)
        val damageDealt = DynamicAmount.EntityProperty(
            EntityReference.Source, EntityNumericProperty.DamageDealtThisTurn
        )
        effect = Effects.Pipeline {
            val before = storeNumber(damageDealt)
            // Fix the radiance group before any damage or damage replacements happen.
            val others = gather(CardSource.BattlefieldMatching(
                filter = GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0)),
                excludeChosenTargets = true
            ))
            run(Effects.DealDamage(DynamicAmount.XValue, victim))
            run(ForEachInCollectionEffect(
                collection = others.key,
                effect = Effects.DealDamage(DynamicAmount.XValue, EffectTarget.Self)
            ))
            run(Effects.GainLife(DynamicAmount.Subtract(damageDealt, before.amount)))
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "194"
        artist = "Dave Dorman"
        flavorText = "\"Let the pyres of the unbelievers light our way.\"\n—Razia"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a3acf27-5d07-48e6-8e19-a2e6d4cd49d1.jpg?1783943626"
        ruling("2005-10-01", "You gain life equal to the total damage dealt by Brightflame to all creatures. You don’t gain life for any damage that was prevented.")
        ruling("2005-10-01", "All creatures that share a color are affected, even your own.")
        ruling("2005-10-01", "A creature “shares a color” with any creature that is at least one of its colors. For example, a green-white creature shares a color with creatures that are green, white, green-white, red-white, black-green, and so on.")
        ruling("2005-10-01", "If it targets a colorless creature, it doesn’t affect any other creatures. A colorless creature shares a color with nothing, not even other colorless creatures.")
        ruling("2005-10-01", "You check which creatures share a color with the target when the spell resolves.")
        ruling("2005-10-01", "Only one creature is targeted. If that creature leaves the battlefield or otherwise becomes an illegal target, the entire spell doesn’t resolve. No other creatures are affected.")
    }
}
