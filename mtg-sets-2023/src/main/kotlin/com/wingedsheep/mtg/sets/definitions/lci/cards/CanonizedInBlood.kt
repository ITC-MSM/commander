package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Canonized in Blood — {1}{B}
 * Enchantment
 * Uncommon — LCI #96
 *
 * At the beginning of your end step, if you descended this turn, put a +1/+1 counter on target
 * creature you control. (You descended if a permanent card was put into your graveyard from
 * anywhere.)
 * {5}{B}{B}, Sacrifice this enchantment: Create a 4/3 white and black Vampire Demon creature
 * token with flying.
 *
 * "Descended this turn" is CR 700.11: at least one nontoken permanent card was put into your
 * graveyard from any zone this turn. The intervening-if gate on the triggered ability fires only
 * once per end step regardless of how many times you descended that turn, and cannot trigger if
 * you have not descended as the end step begins.
 */
val CanonizedInBlood = card("Canonized in Blood") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your end step, if you descended this turn, put a +1/+1 counter on target creature you control. (You descended if a permanent card was put into your graveyard from anywhere.)\n" +
        "{5}{B}{B}, Sacrifice this enchantment: Create a 4/3 white and black Vampire Demon creature token with flying."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        val t = target("target creature you control", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}{B}{B}"), Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 4,
            toughness = 3,
            colors = setOf(Color.WHITE, Color.BLACK),
            creatureTypes = setOf("Vampire", "Demon"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/3/0/3005eb0a-5c96-4a07-a6b9-a907d1095cdf.jpg?1783913605",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/384b6892-5dfc-4607-b511-cf83544a9357.jpg?1782694534"
        ruling(
            "2023-11-10",
            "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn."
        )
        ruling(
            "2023-11-10",
            "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended."
        )
        ruling(
            "2023-11-10",
            "Abilities that begin with \"At the beginning of your end step, if you descended this turn\" will trigger only once during your end step, no matter how many times you descended this turn. However, if you haven't descended this turn as your end step begins, the ability won't trigger at all. It's not possible to put a permanent card into your graveyard during the end step in time to have the ability trigger."
        )
    }
}
