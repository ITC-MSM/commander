package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dryad Arbor — Future Sight #174
 * Land Creature — Forest Dryad · 1/1 · Uncommon
 *
 * (This land isn't a spell, it's affected by summoning sickness, and it has "{T}: Add {G}.")
 *
 * Modeling notes:
 *  - Printed with a green [colorIndicator] (2013-06-07 ruling): with no mana cost, Dryad Arbor
 *    would otherwise be colorless, so the color comes from the indicator, not from a mana cost
 *    that doesn't exist. `colorIdentity` follows it (CR 903.4).
 *  - `typeLine = "Land Creature — Forest Dryad"` is a plain dual-type parse — [TypeLine.parse]
 *    already unions every recognized card type word, so `isLand` and `isCreature` are both true
 *    with no engine change. "Forest" is a land type, "Dryad" a creature type (both ruled on
 *    2021-03-19), so a land-type-changing effect (Sea's Claim) still leaves it a green Dryad.
 *  - The reminder text is just confirming default engine behavior, not modeling anything new:
 *    lands are never cast (so "isn't a spell" needs no code), and every creature already gets
 *    summoning sickness on entry. It is played as a land like any other, using up the turn's land
 *    drop, and it can't be responded to.
 *  - The mana ability is the plain "{T}: Add {G}" every Forest has, just spelled out with
 *    [Effects.AddMana] instead of the `basicLand()` DSL (Dryad Arbor isn't a basic land type
 *    itself, even though "Forest" is one of its subtypes).
 */
val DryadArbor = card("Dryad Arbor") {
    manaCost = ""
    colorIdentity = "G"
    colorIndicator = "G"
    typeLine = "Land Creature — Forest Dryad"
    power = 1
    toughness = 1
    oracleText = "(This land isn't a spell, it's affected by summoning sickness, and it has " +
        "\"{T}: Add {G}.\")"

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN, 1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {G}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Eric Fortune"
        flavorText = "\"Touch no tree, break no branch, and speak only the question you wish " +
            "answered.\"\n—Von Yomm, elder druid, to her initiates"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cee476d-42e1-4997-87af-73e18f542167.jpg?1783943089"
        ruling(
            "2021-03-19",
            "Due to its color indicator (appearing to the left of its type line), Dryad Arbor is " +
                "green. Color indicators apply in all zones, not just the battlefield."
        )
        ruling(
            "2021-03-19",
            "If Dryad Arbor is changed into another basic land type, it continues to be a green " +
                "Dryad creature."
        )
        ruling(
            "2021-03-19",
            "Dryad Arbor is played as a land. It doesn't use the stack, it's not a spell, it " +
                "can't be responded to, it has no mana cost, and it counts as your land play for " +
                "the turn."
        )
    }
}
