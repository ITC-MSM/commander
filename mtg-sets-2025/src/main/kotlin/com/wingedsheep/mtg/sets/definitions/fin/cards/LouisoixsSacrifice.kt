package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Louisoix's Sacrifice — Final Fantasy #59
 * {U} · Instant
 *
 * As an additional cost to cast this spell, sacrifice a legendary creature or pay {2}.
 * Counter target activated ability, triggered ability, or noncreature spell.
 *
 * The additional cost is the alternative "sacrifice a [filter] or pay {N}" shape, modeled with
 * [Costs.additional.SacrificeOrPay]: the enumerator offers two cast paths — sacrifice a legendary
 * creature you control (base {U}), or pay {2} on top of the base cost. With no legendary creature
 * to sacrifice, only the pay path is offered (CR 601.2b/f — the player picks which way to pay).
 *
 * The target is the union "(any activated/triggered ability) OR (noncreature spell)" — a creature
 * spell is NOT a legal target, while abilities and noncreature spells are. Expressed as
 * [TargetFilter.anyOf] of the prebuilt stack-ability and noncreature-spell clauses; the targeting
 * system enumerates each clause and unions the results (TargetFinder), and the counter dispatches
 * by what's actually on the stack at resolution via [Effects.CounterSpellOrAbility].
 */
val LouisoixsSacrifice = card("Louisoix's Sacrifice") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a legendary creature or pay {2}.\n" +
        "Counter target activated ability, triggered ability, or noncreature spell."

    additionalCost(
        Costs.additional.SacrificeOrPay(
            filter = GameObjectFilter.Creature.legendary(),
            alternativeManaCost = "{2}",
        )
    )

    spell {
        target = TargetObject(
            filter = TargetFilter.anyOf(
                TargetFilter.ActivatedOrTriggeredAbilityOnStack,
                TargetFilter.NoncreatureSpellOnStack,
            )
        )
        effect = Effects.CounterSpellOrAbility()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "Mintautas Šukys"
        flavorText = "\"Thank you... for everything... Pray take your rest, Grandfather... You deserve it.\"\n" +
            "—Alisaie Leveilleur"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a6976f2-0bd5-449a-8fcf-f5a732ce22c1.jpg?1782686550"
    }
}
