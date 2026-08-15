package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Weapons Vendor
 * {3}{W}
 * Creature — Human Artificer
 * 2/2
 * When this creature enters, draw a card.
 * At the beginning of combat on your turn, if you control an Equipment, you may pay {1}. When
 *   you do, attach target Equipment you control to target creature you control.
 *
 * The combat ability is the Spellbook Vendor shape: an intervening-"if" gates the trigger on
 * controlling an Equipment, then [MayPayManaEffect] models the optional {1} payment whose
 * "when you do" reflexive ability chooses its targets as it goes on the stack (Scryfall
 * ruling). The reflexive payoff reuses [Effects.AttachTargetEquipmentToCreature], moving the
 * chosen Equipment onto the chosen creature.
 */
val WeaponsVendor = card("Weapons Vendor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw a card.\n" +
        "At the beginning of combat on your turn, if you control an Equipment, you may pay {1}. " +
        "When you do, attach target Equipment you control to target creature you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerCondition = Conditions.YouControl(
            GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
        )
        val equipment = target(
            "target Equipment you control",
            TargetPermanent(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).youControl()
                )
            )
        )
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.AttachTargetEquipmentToCreature(equipment, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Mushk Rizvi"
        flavorText = "\"Man in your line of work needs weapons, no? Why not try that one on for size?\""
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9e6b374-3e44-4df7-b0a3-4ef98dc08267.jpg?1748705905"
    }
}
