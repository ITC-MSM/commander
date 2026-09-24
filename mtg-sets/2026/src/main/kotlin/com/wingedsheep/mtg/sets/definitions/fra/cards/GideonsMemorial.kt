package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Gideon's Memorial (Reality Fracture #198) — {1}{W} Legendary Artifact.
 *
 * Intangible Virtue's anthem at +1/+0, a planeswalker-only mana rock, and Eiganjo's channel
 * ability ("{1}{W}, Discard this card: 4 damage to target attacking or blocking creature"),
 * which is activated from hand ([Zone.HAND]) with the discard as its cost.
 */
val GideonsMemorial = card("Gideon's Memorial") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Artifact"
    oracleText = "Creature tokens you control get +1/+0 and have vigilance.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast a planeswalker spell.\n" +
        "{1}{W}, Discard this card: It deals 4 damage to target attacking or blocking creature."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.token().youControl())
        )
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, GroupFilter(GameObjectFilter.Creature.token().youControl()))
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            amount = 1,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(CardType.PLANESWALKER)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        val t = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = Effects.DealDamage(4, t)
        description = "{1}{W}, Discard this card: It deals 4 damage to target attacking or blocking creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "198"
        artist = "Manuel Castañón"
        flavorText = "Inscribed are the words \"I will keep watch.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/6/768c0e64-9907-417a-a763-c836fdf36883.jpg?1789127685"
        inBooster = false
    }
}
