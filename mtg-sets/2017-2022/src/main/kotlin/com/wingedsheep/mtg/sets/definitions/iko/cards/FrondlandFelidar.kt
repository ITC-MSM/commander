package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Frondland Felidar
 * {2}{G}{W}
 * Creature — Cat Beast
 * 3/5
 * Vigilance
 * Creatures you control with vigilance have "{1}, {T}: Tap target creature."
 *
 * A lord-shaped [GrantActivatedAbility]: the granted ability lives on each matching creature, so
 * its {T} taps *that* creature and it answers to that creature's own summoning sickness. The
 * Felidar grants to itself too — its printed vigilance puts it inside its own filter — and
 * vigilance is what makes the pair work, since an attacker that stays untapped can still tap for
 * the ability afterwards. The filter is re-read live, so a creature that gains or loses vigilance
 * gains or loses the ability with it.
 */
val FrondlandFelidar = card("Frondland Felidar") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Cat Beast"
    power = 3
    toughness = 5
    oracleText = "Vigilance\n" +
        "Creatures you control with vigilance have \"{1}, {T}: Tap target creature.\""

    keywords(Keyword.VIGILANCE)

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap),
                effect = Effects.Tap(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(TargetCreature())
            ),
            filter = GroupFilter(
                GameObjectFilter.Creature.withKeyword(Keyword.VIGILANCE).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "186"
        artist = "Steve Prescott"
        flavorText = "\"Fear not the behemoth heard from miles away. Fear the stalking felidar, which reveals itself only for the kill.\"\n—Rielle, the Everwise"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab220695-e1a9-45ec-a1b1-5a82c9c90a03.jpg"
    }
}
