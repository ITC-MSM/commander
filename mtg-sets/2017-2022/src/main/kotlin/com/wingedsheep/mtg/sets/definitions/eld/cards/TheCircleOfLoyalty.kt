package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * The Circle of Loyalty
 * {4}{W}{W}
 * Legendary Artifact
 * Affinity for Knights (This spell costs {1} less to cast for each Knight you control.)
 * Creatures you control get +1/+1.
 * Whenever you cast a legendary spell, create a 2/2 white Knight creature token with vigilance.
 * {3}{W}, {T}: Create a 2/2 white Knight creature token with vigilance.
 *
 * Four existing primitives: [KeywordAbility.AffinityForSubtype] (Riders of the Mark), a static
 * [ModifyStats] anthem over `Creature.youControl()` (Glorious Anthem), `Triggers.youCastSpell`
 * with a legendary spell filter (Venat, Heart of Hydaelyn), and Aryel's token-making activated
 * ability.
 */
val TheCircleOfLoyalty = card("The Circle of Loyalty") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Artifact"
    oracleText = "Affinity for Knights (This spell costs {1} less to cast for each Knight you control.)\nCreatures you control get +1/+1.\nWhenever you cast a legendary spell, create a 2/2 white Knight creature token with vigilance.\n{3}{W}, {T}: Create a 2/2 white Knight creature token with vigilance."

    // Affinity for Knights
    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.KNIGHT))

    // Creatures you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
        )
    }

    // Whenever you cast a legendary spell, create a 2/2 white Knight creature token with vigilance.
    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Any.legendary())
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE),
        )
    }

    // {3}{W}, {T}: Create a 2/2 white Knight creature token with vigilance.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "9"
        artist = "Bastien L. Deharme"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79093d00-362d-4d07-8a0a-cf5e1ccf9c0f.jpg?1783932677"
        ruling("2019-10-04", "The Circle of Loyalty's triggered ability resolves before the spell that caused it to trigger. It resolves even if that spell is countered.")
        ruling("2019-10-04", "The Circle of Loyalty's triggered ability won't trigger when you cast it because it's not on the battlefield yet.")
        ruling("2019-10-04", "To determine the total cost of a spell, start with the mana cost or alternative cost you're paying, add any cost increases, then apply any cost reductions. The mana value of the spell remains unchanged, no matter what the total cost to cast it was.")
        ruling("2019-10-04", "The cost reduction ability reduces only the generic mana in the relic's cost. The colored mana must still be paid.")
        ruling("2019-10-04", "Once you announce that you're casting a spell, no player may take actions until the spell has been paid for. Notably, opponents can't try to change by how much a relic's cost is reduced.")
    }
}
