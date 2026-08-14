package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Havengul Runebinder
 * {2}{U}{U}
 * Creature — Human Wizard
 * 2/2
 * {2}{U}, {T}, Exile a creature card from your graveyard: Create a 2/2 black Zombie creature token,
 * then put a +1/+1 counter on each Zombie creature you control.
 */
val HavengulRunebinder = card("Havengul Runebinder") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText =
        "{2}{U}, {T}, Exile a creature card from your graveyard: Create a 2/2 black Zombie " +
            "creature token, then put a +1/+1 counter on each Zombie creature you control."
    power = 2
    toughness = 2

    activatedAbility {
        cost = AbilityCost.Composite(
            listOf(
                Costs.Mana(ManaCost.parse("{2}{U}")),
                AbilityCost.Tap,
                Costs.ExileFromGraveyard(count = 1, filter = GameObjectFilter.Creature),
            ),
        )
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Zombie"),
            ),
            Effects.ForEachInGroup(
                filter = GroupFilter(
                    GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE).youControl(),
                ),
                effect = Effects.AddCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    1,
                    EffectTarget.Self,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Bud Cook"
        imageUri =
            "https://cards.scryfall.io/normal/front/d/e/de766c12-eb2c-466a-8630-8242a153eb1f.jpg?1783940998"
    }
}
