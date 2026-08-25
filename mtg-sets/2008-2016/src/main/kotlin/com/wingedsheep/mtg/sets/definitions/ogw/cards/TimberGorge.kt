package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Timber Gorge
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {G}.
 *
 * "Add {R} or {G}" is two separate mana abilities, not one ability with a choice — that is how the
 * dual lands are modelled throughout the corpus, so the player picks by activating the one they
 * want and each carries `manaAbility = true` (no stack, CR 605.3).
 */
val TimberGorge = card("Timber Gorge") {
    colorIdentity = "GR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R} or {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Cliff Childs"
        flavorText = "Tazeem's embrace is harsh, but for those that call it home, nothing else will do."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55d05ff7-f071-4eb3-b10a-203741abdf10.jpg"
    }
}
