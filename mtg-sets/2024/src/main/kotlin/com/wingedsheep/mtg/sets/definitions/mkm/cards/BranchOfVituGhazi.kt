package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Branch of Vitu-Ghazi — Murders at Karlov Manor #258
 * Land
 *
 * Although this is a land, disguise permits it to be cast face down as the standard colorless 2/2
 * creature with ward {2}. Turning it face up is legal because the disguise procedure exposes the
 * land's printed disguise cost; after the special action, the permanent is simply a land again.
 *
 * Its trigger adds two mana of one chosen color with the engine's end-of-turn expiry, so exactly
 * that mana persists through intervening step and phase boundaries. The ordinary colorless mana
 * ability remains a mana ability and therefore does not use the stack.
 */
val BranchOfVituGhazi = card("Branch of Vitu-Ghazi") {
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "Disguise {3} (You may cast this card face down for {3} as a 2/2 creature with ward {2}. " +
        "Turn it face up any time for its disguise cost.)\n" +
        "When this land is turned face up, add two mana of any one color. Until end of turn, you " +
        "don't lose this mana as steps and phases end."

    disguise = "{3}"

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        effect = Effects.AddManaOfChoice(amount = 2)
        description = "When this land is turned face up, add two mana of any one color. Until end " +
            "of turn, you don't lose this mana as steps and phases end."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/" +
            "73a8169f-b858-47a5-9c76-2e7c50ad4ecd.jpg?1783912825"
        ruling(
            "2024-02-02",
            "Any time you have priority, you may turn the face-down creature face up by revealing " +
                "what its disguise cost is and paying that cost. This is a special action. It " +
                "doesn't use the stack and can't be responded to. Only a face-down permanent can " +
                "be turned face up this way; a face-down spell cannot.",
        )
        ruling(
            "2024-02-02",
            "Because the permanent is on the battlefield both before and after it's turned face " +
                "up, turning a permanent face up doesn't cause any enters-the-battlefield " +
                "abilities to trigger.",
        )
    }
}
