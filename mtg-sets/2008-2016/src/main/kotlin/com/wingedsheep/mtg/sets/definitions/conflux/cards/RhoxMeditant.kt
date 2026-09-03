package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Rhox Meditant
 * {3}{W}
 * Creature — Rhino Monk
 * 2/4
 * When this creature enters, if you control a green permanent, draw a card.
 *
 * The "if" is an intervening-if clause (CR 603.4), not a condition on the effect: it is checked
 * both when the trigger would go on the stack and again on resolution, which is what
 * `interveningIf` means here rather than folding the check into the effect. The check itself is
 * [Conditions.YouControl] over `GameObjectFilter.Permanent.withColor(GREEN)` — a bare "permanent"
 * noun, so no creature narrowing.
 */
val RhoxMeditant = card("Rhox Meditant") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rhino Monk"
    power = 2
    toughness = 4
    oracleText = "When this creature enters, if you control a green permanent, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.GREEN))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Donato Giancola"
        flavorText = "The weight of her conviction balances on the harmony of her soul."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b74f4f8d-2191-4743-aac6-cdcb4a68379c.jpg"
    }
}
