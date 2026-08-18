package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deranged Assistant
 * {1}{U}
 * Creature — Human Wizard
 * 1/1
 *
 * {T}, Mill a card: Add {C}.
 *
 * The mill is part of the *cost*, not the effect — `Costs.MillCard` (CostAtom.Mill). Per CR 701.17b
 * the ability can't be activated at all with an empty library, so the mill cost gates legal-action
 * enumeration rather than fizzling at resolution.
 *
 * Not a mana ability. CR 605.1a (August 7, 2026) added "and its **cost** and effect don't move any
 * card to or from a library" to the criteria, and the mill cost is exactly that — so this is an
 * ordinary activated ability: it uses the stack, can be responded to, and can't be activated while
 * paying a cost. The 2025 ruling below turns on the pre-update classification (a mana ability's cost
 * is paid on activation and can't be reversed when the spell being cast is); it is kept because it
 * is what Scryfall still carries, but it no longer describes how the card plays.
 */
val DerangedAssistant = card("Deranged Assistant") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "{T}, Mill a card: Add {C}. (To mill a card, put the top card of your library " +
        "into your graveyard.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.MillCard)
        effect = Effects.AddColorlessMana(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Nils Hamm"
        flavorText = "\"Garl, adjust the slurry dispensers. Garl, fetch more corpses. Garl, quit " +
            "crying and give me your brain tissue. If he doesn't stop being so rude, I'm quitting.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4c03171-5ff0-4f79-bb03-16decf7d34ce.jpg?1783940977"
        ruling(
            "2025-01-24",
            "Once Deranged Assistant's ability has been activated, it can't be reversed for any " +
                "reason. If you activate it while casting a spell and discover you can't produce " +
                "enough mana to pay that spell's costs, the spell is reversed, but Deranged " +
                "Assistant's ability isn't — you'll still have the mana it produced and the milled " +
                "card will still be in your graveyard."
        )
    }
}
