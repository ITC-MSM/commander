package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantWard
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Yuna, Hope of Spira — Final Fantasy #250
 * {3}{G}{W} · Legendary Creature — Human Cleric · 3/5
 *
 * During your turn, Yuna and enchantment creatures you control have trample, lifelink, and ward {2}.
 * At the beginning of your end step, return up to one target enchantment card from your graveyard
 * to the battlefield with a finality counter on it.
 *
 * Clause 1 is a conditional ("during your turn") anthem — the FreyaCrescent shape
 * (`staticAbility { condition = Conditions.IsYourTurn }`). It needs two scopes because Yuna is not
 * herself an enchantment creature and there is no battlefield "is source" predicate to union her
 * into the group: [Filters.Self] for Yuna + a `GroupFilter` of enchantment creatures you control.
 * Ward-with-cost is granted via [GrantWard] ([WardCost.Mana]); enforcement reads the configured cost.
 *
 * Clause 2 is the Rydia "return … with a finality counter" idiom: an end-step trigger with an
 * optional ("up to one") graveyard target, moved GRAVEYARD → BATTLEFIELD then given a
 * [Counters.FINALITY] counter (whose die→exile replacement is engine-intrinsic).
 */
val YunaHopeOfSpira = card("Yuna, Hope of Spira") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Cleric"
    power = 3
    toughness = 5
    oracleText = "During your turn, Yuna and enchantment creatures you control have trample, " +
        "lifelink, and ward {2}.\n" +
        "At the beginning of your end step, return up to one target enchantment card from your " +
        "graveyard to the battlefield with a finality counter on it. (If a permanent with a " +
        "finality counter on it would be put into a graveyard from the battlefield, exile it instead.)"

    // "enchantment creatures you control"
    val enchantmentCreatures = GroupFilter(GameObjectFilter.Creature.youControl() and GameObjectFilter.Enchantment)

    // Clause 1: "During your turn, Yuna and enchantment creatures you control have trample,
    // lifelink, and ward {2}." Self + group are separate scopes (Yuna is not an enchantment creature).
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantKeyword(Keyword.TRAMPLE, Filters.Self) }
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantKeyword(Keyword.TRAMPLE, enchantmentCreatures) }
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantKeyword(Keyword.LIFELINK, Filters.Self) }
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantKeyword(Keyword.LIFELINK, enchantmentCreatures) }
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantWard(WardCost.Mana("{2}"), Filters.Self) }
    staticAbility { condition = Conditions.IsYourTurn; ability = GrantWard(WardCost.Mana("{2}"), enchantmentCreatures) }

    // Clause 2: end-step return of an enchantment card with a finality counter.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        val enchantment = target(
            "up to one target enchantment card from your graveyard",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Enchantment.ownedByYou(), zone = Zone.GRAVEYARD),
                optional = true
            )
        )
        effect = Effects.Move(enchantment, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            .then(AddCountersEffect(Counters.FINALITY, 1, enchantment))
        description = "At the beginning of your end step, return up to one target enchantment card " +
            "from your graveyard to the battlefield with a finality counter on it."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "250"
        artist = "NINNIN"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35b613ad-86f0-431b-af93-147d21041fde.jpg?1748706729"
    }
}
