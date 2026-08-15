package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Improvised Arsenal
 * {1}{R}
 * Artifact — Equipment
 *
 * Equipped creature gets +1/+0 for each artifact you control.
 * {4}{R}: Create a token that's a copy of this Equipment.
 * Equip {R}
 */
val ImprovisedArsenal = card("Improvised Arsenal") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 for each artifact you control.\n{4}{R}: Create a token that's a copy of this Equipment.\nEquip {R}"

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Artifact),
            toughnessBonus = DynamicAmount.Fixed(0)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{4}{R}")
        effect = Effects.CreateTokenCopyOfSelf()
    }

    equipAbility("{R}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "Leanna Crossan"
        flavorText = "\"I want rock 'n' roll, man! Lucky I brought my own drumsticks . . .\"\n—Casey Jones"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/003265d9-b2fc-4916-afc8-53ed2d6aa053.jpg?1769006146"
    }
}
