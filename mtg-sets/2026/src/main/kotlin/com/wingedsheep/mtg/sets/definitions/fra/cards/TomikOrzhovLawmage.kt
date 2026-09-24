package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AttackerCountLimit
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Tomik, Orzhov Lawmage — Reality Fracture #206
 * {1}{W} · Legendary Creature — Human Advisor · 2/1
 *
 * Flying
 * Planeswalkers you control have "No more than one creature can attack this planeswalker each
 * combat."
 * {T}: Target creature with a +1/+1 counter on it gains flying until end of turn.
 *
 * The planeswalker clause is the per-defender axis of [AttackerCountLimit]: each planeswalker Tomik's
 * controller controls may be the attacked defender of at most one creature per combat. It caps only
 * attacks *on* those planeswalkers — attacking the player, or another player's planeswalker, is
 * unaffected — and ends the moment Tomik leaves the battlefield.
 */
val TomikOrzhovLawmage = card("Tomik, Orzhov Lawmage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Advisor"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "Planeswalkers you control have \"No more than one creature can attack this planeswalker " +
        "each combat.\"\n" +
        "{T}: Target creature with a +1/+1 counter on it gains flying until end of turn."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = AttackerCountLimit(maxAttackers = 1, defenders = GroupFilter.PlaneswalkersYouControl)
    }

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target creature with a +1/+1 counter on it",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withCounter(Counters.PLUS_ONE_PLUS_ONE)))
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
        description = "{T}: Target creature with a +1/+1 counter on it gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "206"
        artist = "Randy Vargas"
        flavorText = "\"Many wield the law as a cudgel. I prefer to use it as a shield.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7ca95235-6e54-4ff8-bc2e-6a3d483ff007.jpg?1789729766"
        inBooster = false
    }
}
