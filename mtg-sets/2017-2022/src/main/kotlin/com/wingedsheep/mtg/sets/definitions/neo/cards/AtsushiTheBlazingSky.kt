package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.ModalEffect

/**
 * Atsushi, the Blazing Sky — Kamigawa: Neon Dynasty #134 (canonical printing)
 * {2}{R}{R} · Legendary Creature — Dragon Spirit · 4/4
 *
 * Flying, trample
 * When Atsushi dies, choose one —
 * • Exile the top two cards of your library. Until the end of your next turn, you may play those cards.
 * • Create three Treasure tokens.
 *
 * The dies trigger is a *modal* one: the mode is locked in when the trigger goes on the stack
 * (CR 603.3c), so the choice between cards and mana is made before anyone can respond. The impulse
 * half is [Patterns.Exile.impulse] with [MayPlayExpiry.UntilEndOfNextTurn], which is why the two
 * cards survive the turn Atsushi died on.
 */
val AtsushiTheBlazingSky = card("Atsushi, the Blazing Sky") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dragon Spirit"
    power = 4
    toughness = 4
    oracleText = "Flying, trample\n" +
        "When Atsushi dies, choose one —\n" +
        "• Exile the top two cards of your library. Until the end of your next turn, you may play those cards.\n" +
        "• Create three Treasure tokens."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Patterns.Exile.impulse(2, MayPlayExpiry.UntilEndOfNextTurn),
                "Exile the top two cards of your library. Until the end of your next turn, you may play those cards.",
            ),
            Mode.noTarget(
                Effects.CreateTreasure(3),
                "Create three Treasure tokens.",
            ),
        )
        description = "When Atsushi dies, choose one — • Exile the top two cards of your library. " +
            "Until the end of your next turn, you may play those cards. • Create three Treasure tokens."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "134"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73b64c17-8a52-4d9d-a28b-7e0e945be059.jpg?1783923871"
    }
}
