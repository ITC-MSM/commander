package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Quickchange
 * {1}{U}
 * Instant
 *
 * Target creature becomes the color or colors of your choice until end of turn.
 * Draw a card.
 *
 * "The color **or colors** of your choice" is [Effects.ChooseColorsThen] — the multi-color form of
 * `ChooseColorThen`, whose decision lets the caster pick any nonempty set of colors (ruling: any
 * single color or any combination, never colorless). [Effects.ChangeColorToChosen] then replaces the
 * target's colors with the whole chosen set in layer 5 until end of turn. The color choice is made
 * on resolution, and only if the target is still legal — an illegal target fizzles the spell, so
 * neither the recolor nor the draw happens.
 */
val Quickchange = card("Quickchange") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature becomes the color or colors of your choice until end of turn.\nDraw a card."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ChooseColorsThen(Effects.ChangeColorToChosen(creature)) then
            Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Christopher Moeller"
        flavorText = "Most Ravnicans lead lives of desperate survival. Those who thrive are " +
            "malleable enough to change with the ever-shifting politics of the guilds."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f90c1a1-679d-45c5-a1d4-53dad05e45fa.jpg?1783943680"
        ruling("2005-10-01", "You can choose any single color or any combination of more than one color. You can't choose colorless.")
        ruling("2005-10-01", "Quickchange won't make an artifact stop being an artifact. It'll just be a colorful artifact.")
    }
}
