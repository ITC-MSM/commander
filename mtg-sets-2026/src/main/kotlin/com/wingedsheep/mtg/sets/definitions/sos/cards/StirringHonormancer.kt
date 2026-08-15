package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stirring Honormancer — Secrets of Strixhaven #234
 * {2}{W}{W/B}{B} · Creature — Rhino Bard · 4/5
 *
 * When this creature enters, look at the top X cards of your library, where X is the number of
 * creatures you control. Put one of those cards into your hand and the rest into your graveyard.
 *
 * The "look at top X, keep one in hand, rest to graveyard" shape is the
 * [Patterns.Library.lookAtTopAndKeep] recipe (Gather top X → Select exactly 1 → Move kept to hand →
 * Move rest to graveyard). X = creatures you control, counting the Honormancer itself since it's on
 * the battlefield when this ETB resolves; [SelectionMode.ChooseExactly] clamps to the size of the
 * gathered pile, so an empty or smaller library is handled gracefully.
 */
val StirringHonormancer = card("Stirring Honormancer") {
    manaCost = "{2}{W}{W/B}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Rhino Bard"
    power = 4
    toughness = 5
    oracleText = "When this creature enters, look at the top X cards of your library, where X is " +
        "the number of creatures you control. Put one of those cards into your hand and the rest " +
        "into your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndKeep(
            count = DynamicAmounts.creaturesYouControl(),
            keepCount = com.wingedsheep.sdk.scripting.values.DynamicAmount.Fixed(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "April Prime"
        flavorText = "\"Speak as though the world will be a better place after your words are in it.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee84b04d-78fc-416f-9166-72e5417c3e17.jpg?1775938634"
    }
}
