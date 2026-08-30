package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mnemonic Nexus — Ravnica: City of Guilds #59
 * {3}{U} · Instant
 *
 * Each player shuffles their graveyard into their library.
 *
 * Modelling notes:
 * - Untargeted and symmetric, so it is [Effects.ForEachPlayer] over [Player.Each] rather than a
 *   target requirement. Inside the iteration `Player.You` rebinds to the iterated player, which is
 *   why the body can reuse the controller-scoped
 *   [com.wingedsheep.sdk.dsl.Patterns.Library.shuffleGraveyardIntoLibrary] recipe unchanged.
 * - The shuffle is part of the move (`ZonePlacement.Shuffled`), not a separate shuffle step, so a
 *   player with an empty graveyard still shuffles nothing rather than shuffling their library.
 */
val MnemonicNexus = card("Mnemonic Nexus") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Each player shuffles their graveyard into their library."

    spell {
        effect = Effects.ForEachPlayer(
            players = Player.Each,
            effects = listOf(Patterns.Library.shuffleGraveyardIntoLibrary(EffectTarget.Controller))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Stephen Tappin"
        flavorText = "True enlightenment comes not with a new thought, but with understanding of " +
            "all the old ones."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/200edda9-7ad9-47fc-837e-37ec9e5b4b51.jpg?1783943682"
    }
}
