package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetSpellOrPermanent

/**
 * Blind Seer
 * {2}{U}{U}
 * Legendary Creature — Human Wizard
 * 3/3
 * {1}{U}: Target spell or permanent becomes the color of your choice until end of turn.
 *
 * Invasion engine gap #11: first card to recolor a spell on the stack. The target uses
 * [TargetSpellOrPermanent] (battlefield permanent or stack spell); the color choice is the
 * existing single-color [Effects.ChooseColorThen] pattern, and [Effects.ChangeColorToChosen]
 * applies a Layer-5 color change that the projector now reads for stack objects too (so a
 * recolored spell's new color drives color-matching checks like protection during resolution).
 */
val BlindSeer = card("Blind Seer") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "{1}{U}: Target spell or permanent becomes the color of your choice until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        val t = target("target", TargetSpellOrPermanent())
        effect = Effects.ChooseColorThen(
            then = Effects.ChangeColorToChosen(t),
            prompt = "Choose a color"
        )
        description = "{1}{U}: Target spell or permanent becomes the color of your choice until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "47"
        artist = "Dave Dorman"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c54ec26-c7f1-4258-9cc9-1709987f293c.jpg?1591291188"
    }
}
