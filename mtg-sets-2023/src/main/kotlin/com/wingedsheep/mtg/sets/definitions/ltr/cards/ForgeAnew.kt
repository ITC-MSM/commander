package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EquipAbilitiesAtInstantSpeed
import com.wingedsheep.sdk.scripting.FreeFirstEquipEachTurn
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Forge Anew
 * {2}{W}
 * Enchantment
 *
 * When this enchantment enters, return target Equipment card from your graveyard to the battlefield.
 * During your turn, you may activate equip abilities any time you could cast an instant.
 * You may pay {0} rather than pay the equip cost of the first equip ability you activate during each
 * of your turns.
 *
 * The two equip-permission clauses use the equipment-aware static abilities
 * [EquipAbilitiesAtInstantSpeed] (gated to your turn via the conditional static) and
 * [FreeFirstEquipEachTurn]; the engine keys both off `ActivatedAbility.isEquipAbility`.
 */
val ForgeAnew = card("Forge Anew") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, return target Equipment card from your graveyard to the battlefield.\n" +
        "During your turn, you may activate equip abilities any time you could cast an instant.\n" +
        "You may pay {0} rather than pay the equip cost of the first equip ability you activate during each of your turns."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val equipment = target(
            "Equipment card in your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefield(equipment)
    }

    // "During your turn, you may activate equip abilities any time you could cast an instant."
    staticAbility {
        condition = Conditions.IsYourTurn
        ability = EquipAbilitiesAtInstantSpeed
    }

    // "You may pay {0} rather than pay the equip cost of the first equip ability you activate
    // during each of your turns."
    staticAbility {
        ability = FreeFirstEquipEachTurn
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56274b88-6e3f-4538-bb0c-eb5e52a58ef3.jpg?1686967804"
    }
}
