package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dovin's Veto — War of the Spark #193 (canonical printing)
 * {W}{U}
 * Instant
 * This spell can't be countered.
 * Counter target noncreature spell.
 *
 * "This spell can't be countered" is a characteristic of the card, not an effect it resolves —
 * it is the card-level [cantBeCountered] flag, which the stack reads while Dovin's Veto is
 * itself on the stack. Negate's body underneath.
 */
val DovinsVeto = card("Dovin's Veto") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\n" +
        "Counter target noncreature spell."

    cantBeCountered = true

    spell {
        target("target", Targets.NoncreatureSpell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "193"
        artist = "Izzy"
        flavorText = "\"I see you've learned nothing, Chandra. You'd still put a match to something rather than understand it.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d6b5054-2224-4f68-9d82-3ed17c5dacc4.jpg"
    }
}
