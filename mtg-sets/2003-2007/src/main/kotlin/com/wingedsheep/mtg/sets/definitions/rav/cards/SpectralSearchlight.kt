package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Spectral Searchlight
 * {3}
 * Artifact
 *
 * {T}: Choose a player. That player adds one mana of any color they choose.
 *
 * "Choose a player" doesn't target, so this is a mana ability (CR 605.1a): it resolves on the spot,
 * off the stack, and can't be responded to. [Effects.ChoosePlayerThenTheyAddManaOfAnyColor] picks
 * the player as the ability resolves, then hands the *color* choice to that player — the mana lands
 * in their pool, of a color they name. Choosing yourself is legal (ruling), and then you name the
 * color. Because both answers belong to the moment of resolution, the activator is never asked for
 * a color up front, and auto-pay never taps it: activate it by hand.
 */
val SpectralSearchlight = card("Spectral Searchlight") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "{T}: Choose a player. That player adds one mana of any color they choose."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.ChoosePlayerThenTheyAddManaOfAnyColor()
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Choose a player. That player adds one mana of any color they choose."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "271"
        artist = "Martina Pilcerova"
        flavorText = "The first searchlights were given as gifts, symbols of cooperation, to the " +
            "emissaries present at the signing of the Guildpact."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b7209e2-8e62-40aa-ad84-16c6ad52fd69.jpg?1783943595"
        ruling("2005-10-01", "You may choose yourself.")
    }
}
