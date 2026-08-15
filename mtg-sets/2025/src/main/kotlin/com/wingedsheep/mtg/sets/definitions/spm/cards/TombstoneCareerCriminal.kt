package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Tombstone, Career Criminal
 * {2}{B}
 * Legendary Creature — Human Villain
 * 2/2
 * When Tombstone enters, return target Villain card from your graveyard to your hand.
 * Villain spells you cast cost {1} less to cast.
 */
val TombstoneCareerCriminal = card("Tombstone, Career Criminal") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Villain"
    power = 2
    toughness = 2
    oracleText = "When Tombstone enters, return target Villain card from your graveyard to your hand.\n" +
        "Villain spells you cast cost {1} less to cast."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val villainCard = target(
            "target Villain card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Any.withSubtype("Villain").ownedByYou(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.ReturnToHand(villainCard)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype("Villain")),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Bartek Fedyczak"
        flavorText = "\"I told you years ago, stay out of my way and you'll live a long, long time. " +
            "That was good advice then, and it's good advice now.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/313189ef-fe6e-4511-9386-920a88a49a88.jpg?1758215907"
    }
}
