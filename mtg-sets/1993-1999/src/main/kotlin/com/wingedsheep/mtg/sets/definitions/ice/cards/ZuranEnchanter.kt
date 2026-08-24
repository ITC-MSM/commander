package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Zuran Enchanter
 * {1}{U}
 * Creature — Human Wizard
 * 1/1
 *
 * {2}{B}, {T}: Target player discards a card. Activate only during your turn.
 *
 * The discard is [Patterns.Hand]'s gather → select → move recipe with the *target player* as both
 * the hand's owner and the chooser — restating those three steps by hand drops the `moveType` and
 * the prompt. "Activate only during your turn" is an [ActivationRestriction], not a
 * `TimingRule`: unlike Wall of Distortion's "only as a sorcery", this one still permits activation
 * at instant speed during your own turn.
 */
val ZuranEnchanter = card("Zuran Enchanter") {
    manaCost = "{1}{U}"
    colorIdentity = "BU"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "{2}{B}, {T}: Target player discards a card. Activate only during your turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Tap)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        val t = target("target", Targets.Player)
        effect = Patterns.Hand.discardCards(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Douglas Shuler"
        flavorText = "\"We are Kjeldorans no more.\"\n—Zur the Enchanter"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/721edcef-f40a-4d43-9d80-26161dc425cb.jpg"
    }
}
