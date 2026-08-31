package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Terrarion
 * {1}
 * Artifact
 *
 * This artifact enters tapped.
 * {2}, {T}, Sacrifice this artifact: Add two mana in any combination of colors.
 * When this artifact is put into a graveyard from the battlefield, draw a card.
 *
 * The sacrifice is part of the *cost*, so the last ability triggers as the cost is paid, while the
 * mana ability itself resolves immediately without using the stack. That is what produces the
 * printed ordering the rulings call out: you choose the colours and get the mana first, and the
 * card is drawn afterwards when the trigger resolves.
 *
 * The trigger is [Triggers.Dies] — battlefield-to-graveyard on the source. It is not creature-only
 * despite the name, which is exactly the "no matter how it's put into a graveyard" the rulings want:
 * self-sacrifice, destruction and any other route to the graveyard all fire it.
 */
val Terrarion = card("Terrarion") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "This artifact enters tapped.\n" +
        "{2}, {T}, Sacrifice this artifact: Add two mana in any combination of colors.\n" +
        "When this artifact is put into a graveyard from the battlefield, draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddManaInAnyCombination(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "Add two mana in any combination of colors."
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
        description = "When this artifact is put into a graveyard from the battlefield, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "273"
        artist = "Luca Zontini"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05c4b07f-c6ed-4a92-aab3-54ee6adfb793.jpg?1783943595"
        ruling("2016-07-13", "You must choose the colors of mana Terrarion produces before its last ability has you draw a card.")
        ruling("2016-07-13", "Terrarion's last ability triggers no matter how it's put into a graveyard from the battlefield.")
        ruling("2005-10-01", "You can get either two mana of the same color or one mana each of different colors.")
    }
}
