package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Meandering River
 * Land
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 *
 * "Add {W} or {U}" is two separate mana abilities, not one ability with a choice — that is how the
 * dual lands are modelled throughout the corpus, so the player picks by activating the one they
 * want and each carries `manaAbility = true` (no stack, CR 605.3).
 */
val MeanderingRiver = card("Meandering River") {
    colorIdentity = "UW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W} or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Cliff Childs"
        flavorText = "The river split into many channels as it flowed to the Halimar Sea. Few travelers could follow the same one twice."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a5bedf1-92b6-465c-afc2-ce8e150a5e57.jpg"
    }
}
