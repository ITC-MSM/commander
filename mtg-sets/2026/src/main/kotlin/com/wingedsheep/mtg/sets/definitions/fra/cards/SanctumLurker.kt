package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val SanctumLurker = card("Sanctum Lurker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, empower Jace 1.\n" +
        "Planeswalkers you control aren't put into their owners' graveyards for having 0 loyalty.\n" +
        "Planeswalkers you control have \"[+2]: This planeswalker deals 1 damage to each opponent and you gain 1 life.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(1)
    }

    staticAbility {
        ability = GrantKeyword(
            AbilityFlag.SURVIVES_ZERO_LOYALTY.name,
            GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(+2) {
                effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
                    .then(Effects.GainLife(1))
                description = "This planeswalker deals 1 damage to each opponent and you gain 1 life."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/2185c08f-bb4d-49d5-8b6c-c629a48bb61c.jpg?1789556729"
        inBooster = false
    }
}
