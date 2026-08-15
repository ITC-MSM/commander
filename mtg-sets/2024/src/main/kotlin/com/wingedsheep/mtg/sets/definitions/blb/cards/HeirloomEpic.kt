package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Heirloom Epic
 * {1}
 * Artifact
 *
 * {4}, {T}: Draw a card. For each mana in this ability's activation cost,
 * you may tap an untapped creature you control rather than pay that mana.
 * Activate only as a sorcery.
 *
 * The "tap creatures to pay mana" mechanic is essentially convoke applied to
 * an activated ability. The hasConvoke flag on the ability enables the engine's
 * convoke payment infrastructure for this ability.
 */
val HeirloomEpic = card("Heirloom Epic") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{4}, {T}: Draw a card. For each mana in this ability's activation cost, you may tap an untapped creature you control rather than pay that mana. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Effects.DrawCards(1)
        timing = TimingRule.SorcerySpeed
        hasConvoke = true
        description = "{4}, {T}: Draw a card (you may tap creatures to help pay)"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Fiona Hsieh"
        flavorText = "\"The beasts feared fire, and little else. Lily of Valley raised Cragflame and...\""
        imageUri = "https://cards.scryfall.io/normal/front/7/8/7839ce48-0175-494a-ab89-9bdfb7a50cb1.jpg?1721427279"

        ruling("2024-07-26", "You can tap any untapped creature you control rather than pay a mana in the activation cost of Heirloom Epic's ability, even one you haven't controlled continuously since the beginning of your most recent turn.")
        ruling("2024-07-26", "You can't tap more creatures than the amount of mana in the cost of Heirloom Epic's ability. Effects that increase or reduce the activation cost of Heirloom Epic's ability apply before costs are paid and will increase or decrease the total number of creatures you can tap.")
    }
}
