package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Clutch of the Undercity
 * {1}{U}{U}{B}
 * Instant
 *
 * Return target permanent to its owner's hand. Its controller loses 3 life.
 * Transmute {1}{U}{B}
 *
 * "Its controller" is read after the bounce has already moved the permanent, so
 * [EffectTarget.TargetController] falls through to the last-known controller of the
 * permanent as it existed on the battlefield (CR 608.2h) rather than to the card's owner —
 * which is what makes the life loss land on the thief when the bounced permanent was stolen.
 */
val ClutchOfTheUndercity = card("Clutch of the Undercity") {
    manaCost = "{1}{U}{U}{B}"
    typeLine = "Instant"
    oracleText = "Return target permanent to its owner's hand. Its controller loses 3 life.\nTransmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "UB"

    spell {
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.ReturnToHand(permanent)
            .then(Effects.LoseLife(3, EffectTarget.TargetController))
    }
    transmute("{1}{U}{B}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24437641-85a1-4fcd-93dc-bd19a7abf969.jpg?1783943625"
    }
}
