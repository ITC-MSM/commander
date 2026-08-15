package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shredder's Armor
 * {1}{B}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+1.
 * When this Equipment enters, attach it to target creature you
 * control.
 * Equip—Sacrifice another nonland permanent. Activate only once each
 * turn.
 */
val ShreddersArmor = card("Shredder's Armor") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+1.\nWhen this Equipment enters, attach it to target creature you control.\nEquip—Sacrifice another nonland permanent. Activate only once each turn."

    staticAbility {
        ability = ModifyStats(2, 1, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
    }

    activatedAbility {
        cost = Costs.SacrificeAnother(
            GameObjectFilter(
                cardPredicates = listOf(
                    CardPredicate.Not(CardPredicate.IsLand),
                )
            )
        )
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Maël Ollivier-Henry"
        flavorText = "The Kuro Kabuto has been the symbol of Foot Clan leadership since its inception over 1,500 years ago."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2cbe512-725f-4884-ac49-a98a78e7d14e.jpg?1771586903"
    }
}
