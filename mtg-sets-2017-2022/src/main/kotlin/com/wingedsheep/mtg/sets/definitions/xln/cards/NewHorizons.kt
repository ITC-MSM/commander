package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility

/**
 * New Horizons
 * {2}{G}
 * Enchantment — Aura
 * Enchant land
 * When this Aura enters, put a +1/+1 counter on target creature you control.
 * Enchanted land has "{T}: Add two mana of any one color."
 *
 * The granted ability is a *separate* mana ability on the land (it doesn't replace or
 * augment the land's own tap-for-mana), so it is modelled as [GrantActivatedAbility]
 * rather than the `AdditionalManaOnTap` (Fertile Ground) shape.
 */
val NewHorizons = card("New Horizons") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\n" +
        "When this Aura enters, put a +1/+1 counter on target creature you control.\n" +
        "Enchanted land has \"{T}: Add two mana of any one color.\""

    auraTarget = Targets.Land

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.CreatureYouControl)
        effect = Effects.AddCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = creature
        )
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(2)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Noah Bradley"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15b12c75-1248-4c81-90cf-28e341a885cf.jpg?1783935723"
        ruling("2019-05-03", "You can cast New Horizons even if you control no creatures.")
        ruling(
            "2019-05-03",
            "If the land this Aura would enchant is an illegal target by the time New Horizons resolves, " +
                "the entire spell doesn't resolve. It won't enter the battlefield, so its ability won't trigger."
        )
    }
}
