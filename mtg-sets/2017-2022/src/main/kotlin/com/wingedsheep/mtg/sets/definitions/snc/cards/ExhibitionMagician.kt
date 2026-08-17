package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect

/**
 * Exhibition Magician
 * {2}{R}
 * Creature — Human Wizard
 * 2 / 1
 * When this creature enters, choose one —
 * • Create a 1/1 green and white Citizen creature token.
 * • Create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 *
 * A "choose one" enters trigger via [ModalEffect.chooseOne] (same shape as Damage Control Crew);
 * neither mode targets, so both are [Mode.noTarget] — the Citizen is the shared
 * [Effects.CreateToken] and the Treasure the predefined [Effects.CreateTreasure].
 */
val ExhibitionMagician = card("Exhibition Magician") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature enters, choose one —\n• Create a 1/1 green and white Citizen creature token.\n• Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.GREEN, Color.WHITE),
                    creatureTypes = setOf("Citizen"),
                ),
                description = "Create a 1/1 green and white Citizen creature token."
            ),
            Mode.noTarget(
                effect = Effects.CreateTreasure(1),
                description = "Create a Treasure token."
            )
        )
        description = "When this creature enters, choose one — Create a 1/1 green and white Citizen creature token. • Create a Treasure token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbb4479f-3157-44b4-b18b-7553c7513118.jpg?1783923118"
    }
}
