package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismissive Pyromancer
 * {1}{R}
 * Creature — Human Wizard
 * 2/2
 * {R}, {T}, Discard a card: Draw a card.
 * {2}{R}, {T}, Sacrifice this creature: It deals 4 damage to target creature.
 *
 * Two ordinary activated abilities; the whole card is cost vocabulary. The second one sacrifices its
 * own source as part of the cost, so "it" is already gone when the ability resolves — the damage is
 * a flat 4, so no last-known-information read is needed.
 */
val DismissivePyromancer = card("Dismissive Pyromancer") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{R}, {T}, Discard a card: Draw a card.\n" +
        "{2}{R}, {T}, Sacrifice this creature: It deals 4 damage to target creature."

    // {R}, {T}, Discard a card: Draw a card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    // {2}{R}, {T}, Sacrifice this creature: It deals 4 damage to target creature.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.Tap, Costs.SacrificeSelf)
        val victim = target("target", Targets.Creature)
        effect = Effects.DealDamage(4, victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "Bram Sels"
        flavorText = "\"Burn. Burn. Keep. Burn.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0d0a9fa-75c9-4492-accd-bc9f79407453.jpg"
    }
}
