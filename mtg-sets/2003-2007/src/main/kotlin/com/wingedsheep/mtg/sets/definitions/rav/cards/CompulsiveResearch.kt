package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Compulsive Research
 * {2}{U}
 * Sorcery
 *
 * Target player draws three cards. Then that player discards two cards unless they discard a land card.
 *
 * The targeted twin of Thirst for Discovery: both halves run on the *target*, not the controller, so
 * the draw and the discard-gate both take the same target handle. The discard is one selection, not
 * two decisions — a single land card satisfies it, otherwise two cards must be selected. Any land
 * card counts, basic or not (unlike Thirst for Discovery, which demands a basic).
 */
val CompulsiveResearch = card("Compulsive Research") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player draws three cards. Then that player discards two cards unless they discard a land card."

    spell {
        val player = target("player", Targets.Player)
        effect = Effects.DrawCards(3, player)
            .then(Effects.DiscardUnlessMatching(2, GameObjectFilter.Land, target = player))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Michael Sutfin"
        flavorText = "\"Four parts molten bronze, yes . . . one part frozen mercury, yes, yes . . . but then what?\""
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20b9c057-b80a-4e4e-a0d7-34b7ff3a69f4.jpg?1783943689"
        ruling(
            "2017-03-14",
            "The target player can discard either one land card or two cards which may or may not be lands. " +
                "The player can discard one land and one other card or two land cards if they choose."
        )
    }
}
