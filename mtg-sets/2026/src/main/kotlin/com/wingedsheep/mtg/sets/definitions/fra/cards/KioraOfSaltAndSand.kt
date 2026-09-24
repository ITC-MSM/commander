package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Kiora of Salt and Sand
 *
 * - "Whenever you attack" fires once per attack declaration; "if you've activated a loyalty
 *   ability this turn" is an intervening if (checked on trigger and on resolution) over the
 *   per-player loyalty-activation turn tracker, so a planeswalker that has since died still counts.
 * - The untap and "can't be blocked this turn" both land on the one target attacking creature —
 *   any attacking creature, not only yours.
 * - The −8 is granted to every planeswalker you control, so it follows the loyalty rules of the
 *   planeswalker it's on.
 */
val KioraOfSaltAndSand = card("Kiora of Salt and Sand") {
    manaCost = "{1}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Merfolk Noble"
    power = 2
    toughness = 4
    oracleText = "Whenever you attack, if you've activated a loyalty ability this turn, untap target " +
        "attacking creature. It can't be blocked this turn.\n" +
        "Planeswalkers you control have \"[−8]: Create an 8/8 blue Leviathan creature token with hexproof.\""

    triggeredAbility {
        trigger = Triggers.YouAttack
        interveningIf = Conditions.YouActivatedLoyaltyAbilityThisTurn()
        val attacker = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.Composite(
            Effects.Untap(attacker),
            Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, attacker),
        )
        description = "Whenever you attack, if you've activated a loyalty ability this turn, untap " +
            "target attacking creature. It can't be blocked this turn."
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-8) {
                effect = Effects.CreateToken(
                    power = 8,
                    toughness = 8,
                    colors = setOf(Color.BLUE),
                    creatureTypes = setOf("Leviathan"),
                    keywords = setOf(Keyword.HEXPROOF),
                    imageUri = "https://cards.scryfall.io/normal/front/a/4/a4339d5a-7c07-4326-b0e6-d2519332803d.jpg?1789735757",
                )
                description = "Create an 8/8 blue Leviathan creature token with hexproof."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl()),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "273"
        artist = "Chris Rahn"
        flavorText = "\"The sea ends where I say it does.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/1/8151f5f5-e9f6-4fbe-b543-f456ebf22aa5.jpg?1789127745"
        inBooster = false
    }
}
