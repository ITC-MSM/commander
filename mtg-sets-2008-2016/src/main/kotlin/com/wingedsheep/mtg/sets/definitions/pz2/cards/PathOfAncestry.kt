package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.ManaSpellRider
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Path of Ancestry
 * Land
 *
 * This land enters tapped.
 * {T}: Add one mana of any color in your commander's color identity. When that mana is
 * spent to cast a creature spell that shares a creature type with your commander, scry 1.
 */
val PathOfAncestry = card("Path of Ancestry") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "This land enters tapped.\n" +
        "{T}: Add one mana of any color in your commander's color identity. " +
        "When that mana is spent to cast a creature spell that shares a creature type " +
        "with your commander, scry 1. (Look at the top card of your library. " +
        "You may put that card on the bottom.)"

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaOfChoiceEffect(
            colorSet = ManaColorSet.CommanderIdentity,
            riders = setOf(ManaSpellRider.ScryOnSharedTypeWithCommander(1)),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65679"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88b9476e-3556-4229-a4fe-84e527894b41.jpg?1562254418"
        ruling("2020-11-10", "If your commander is a card that has no colors in its color identity, Path of Ancestry's ability produces no mana. It doesn't produce {C}.")
        ruling("2020-11-10", "If your commander has no creature types, it can't share a creature type with any spell that you cast.")
        ruling("2020-11-10", "If you cast your commander with mana from Path of Ancestry, and your commander hasn't somehow lost all of its creature types while on the stack, you'll scry 1.")
        ruling("2020-11-10", "If you don't have a commander, Path of Ancestry's ability produces no mana.")
        ruling("2020-11-10", "If Path of Ancestry's last ability produces two mana (most likely due to Mana Reflection), spending those two mana to cast creature spells that share a creature type with your commander will cause two abilities to trigger. Each of those abilities will cause you to scry 1. You won't scry 2.")
        ruling("2023-07-28", "Your commander's creature types are checked immediately after you cast a creature spell spending mana from Path of Ancestry's last ability. They aren't set before the game begins, and they may not be the same types your commander had when you activated that ability.")
        ruling("2023-07-28", "If you have two commanders, the last ability adds one mana of any color in their combined color identities. When you spend that mana on a creature spell that shares a creature type with either of your commanders, you'll scry 1.")
    }
}
