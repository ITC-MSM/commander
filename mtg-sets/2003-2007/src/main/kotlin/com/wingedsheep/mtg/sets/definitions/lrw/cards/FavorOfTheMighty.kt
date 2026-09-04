package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantProtection
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Favor of the Mighty
 * {1}{W}
 * Kindred Enchantment — Giant
 *
 * Each creature with the greatest mana value has protection from each color.
 *
 * Two things about the one sentence:
 *
 *  - **"Each creature" is every creature on the battlefield, not yours.** The group filter carries
 *    no controller predicate on purpose — the enchantment shields an opponent's fatty just as
 *    happily as your own, which is the whole reason it's a Giant card. Aim any test at an
 *    opponent's creature: the implicit "you control" reading passes every same-side assertion.
 *  - **"The greatest mana value" is a global maximum with ties, so it is a state predicate on the
 *    affected set, not a condition on the ability.** The set is recomputed on every projection
 *    pass, which is what the 2007-10-01 ruling ("this effect is checked continuously … if a new
 *    creature enters with the highest mana value, it gains protection and the previous highest
 *    loses it") asks for; a `ConditionalStaticAbility` gate would ask a yes/no question about the
 *    enchantment instead of picking out creatures. `HasGreatestManaValueAmongAllCreatures` is the
 *    mana-value mirror of `HasLeastPowerAmongAllCreatures`, added for this card: ties leave every
 *    creature sharing the maximum in the set, and X in a mana cost counts as 0 (CR 202.3b), both
 *    of which the rulings call out.
 *
 * Protection from each color is five [GrantProtection] statics, one per color — the same shape
 * Mask of Law and Grace pairs for two colors, and the static counterpart of the five keyword
 * grants Sygg, Wanderbrine Shield composes for the until-your-next-turn version. There is no
 * "from each color" [com.wingedsheep.sdk.scripting.ProtectionScope]; `Colors(Color.entries)` would
 * need one continuous effect to carry five qualities, and five independent effects is both the
 * existing house shape and how the projection reads them back.
 */
private val greatestManaValueCreatures = GroupFilter(
    GameObjectFilter.Creature.hasGreatestManaValueAmongAllCreatures()
)

val FavorOfTheMighty = card("Favor of the Mighty") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Kindred Enchantment — Giant"
    oracleText = "Each creature with the greatest mana value has protection from each color."

    Color.entries.forEach { color ->
        staticAbility {
            ability = GrantProtection(color, greatestManaValueCreatures)
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Larry MacDougall"
        flavorText = "\"What does a mountain fear of a fly? Giants are barely aware of us, let " +
            "alone afraid.\"\n—Gaddock Teeg"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20d858be-4c03-4819-94e9-ca4ca3de9032.jpg?1783942915"
        ruling(
            "2007-10-01",
            "If there's a tie for highest mana value, all of those creatures have protection " +
                "from all colors."
        )
        ruling(
            "2007-10-01",
            "This effect is checked continuously. If a new creature enters with the highest mana " +
                "value, it gains protection from all colors and the previous highest loses it."
        )
        ruling(
            "2007-10-01",
            "If a creature has X in its mana cost, that X is treated as 0 for the purposes of " +
                "this effect. It doesn't matter what the value of X was while the creature was " +
                "on the stack."
        )
        ruling(
            "2024-06-07",
            "This cards was originally printed with the \"tribal\" card type. That card type has " +
                "been replaced with \"kindred\". This change does not affect the gameplay " +
                "function of this card."
        )
    }
}
