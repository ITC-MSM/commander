package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Jaspera Sentinel
 * {G}
 * Creature — Elf Rogue
 * 1/2
 * Reach
 * {T}, Tap an untapped creature you control: Add one mana of any color.
 *
 * A two-body mana dork: the cost taps the Sentinel *and* another untapped creature you control, so
 * it needs a partner to produce anything. [Costs.TapAnotherPermanent] carries the "another" — the
 * Sentinel cannot pay its own second half.
 */
val JasperaSentinel = card("Jaspera Sentinel") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Rogue"
    oracleText = "Reach\n" +
        "{T}, Tap an untapped creature you control: Add one mana of any color."
    power = 1
    toughness = 2

    keywords(Keyword.REACH)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.TapAnotherPermanent(GameObjectFilter.Creature.youControl())
        )
        effect = Effects.AddManaOfChoice()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Raoul Vitale"
        flavorText = "The arrows of the elvish elite strike as true and deadly as the fangs of the Cosmos Serpent they revere."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a68615d-9808-479d-aa80-50651246954e.jpg"
    }
}
