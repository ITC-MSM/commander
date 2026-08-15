package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Electroduplicate
 * {2}{R}
 * Sorcery
 * Create a token that's a copy of target creature you control, except it has haste and
 * "At the beginning of the end step, sacrifice this token."
 * Flashback {2}{R}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * Red's aggressive cousin of Self-Reflection: the copy clause layers `addedKeywords = HASTE`
 * and the "sacrifice at the next end step" delayed trigger (`sacrificeAtStep = END`, any player's
 * end step — the token dies before it can attack again) onto [Effects.CreateTokenCopyOfTarget].
 */
val Electroduplicate = card("Electroduplicate") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target creature you control, except it has haste and " +
        "\"At the beginning of the end step, sacrifice this token.\"\n" +
        "Flashback {2}{R}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.CreateTokenCopyOfTarget(
            creature,
            addedKeywords = setOf(Keyword.HASTE),
            sacrificeAtStep = Step.END
        )
    }
    keywordAbility(KeywordAbility.flashback("{2}{R}{R}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "85"
        artist = "Warren Mahy"
        flavorText = "The dragon roared and the storm thundered back."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abb06b1c-5d4e-49b9-9c4a-e60ab656a257.jpg?1782689192"
    }
}
