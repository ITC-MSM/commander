package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Valduk, Keeper of the Flame
 * {2}{R}
 * Legendary Creature — Human Shaman
 * 3/2
 * At the beginning of combat on your turn, for each Aura and Equipment attached to
 * Valduk, Keeper of the Flame, create a 3/1 red Elemental creature token with trample
 * and haste. Exile those tokens at the beginning of the next end step.
 */
val ValdukKeeperOfTheFlame = card("Valduk, Keeper of the Flame") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Shaman"
    power = 3
    toughness = 2
    oracleText = "At the beginning of combat on your turn, for each Aura and Equipment attached to Valduk, Keeper of the Flame, create a 3/1 red Elemental creature token with trample and haste. Exile those tokens at the beginning of the next end step."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = CreateTokenEffect(
            count = DynamicAmounts.attachmentsOnSelf(),
            power = 3,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Elemental"),
            keywords = setOf(Keyword.TRAMPLE, Keyword.HASTE),
            exileAtStep = Step.END,
            imageUri = "https://cards.scryfall.io/normal/front/9/b/9b9cc37a-b9c3-40c2-a3e4-abfec86e199b.jpg?1562702213"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "148"
        artist = "Victor Adame Minguez"
        flavorText = "\"The hammer strikes. The Flame awakens.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/5/253f9e43-5bc6-4f26-a8e9-773cd0ca3d02.jpg?1562732810"
        ruling("2022-12-08", "Valduk counts all Auras and Equipment attached to it, not only Auras and Equipment you control.")
        ruling("2022-12-08", "If Valduk leaves the battlefield after its ability has triggered but before it resolves, use the number of Auras and Equipment that were last attached to it before it left the battlefield to determine how many tokens to create.")
        ruling("2022-12-08", "If Valduk leaves the battlefield after its ability has resolved, the tokens are still exiled at the beginning of the next end step.")
    }
}
