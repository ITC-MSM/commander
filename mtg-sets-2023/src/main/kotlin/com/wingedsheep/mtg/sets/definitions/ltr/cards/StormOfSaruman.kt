package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CopyTargetSpellEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Storm of Saruman
 * {4}{U}{U}
 * Enchantment
 *
 * Ward {3}
 * Whenever you cast your second spell each turn, copy it, except the copy isn't legendary.
 * You may choose new targets for the copy. (A copy of a permanent spell becomes a token.)
 */
val StormOfSaruman = card("Storm of Saruman") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Ward {3}\n" +
        "Whenever you cast your second spell each turn, copy it, except the copy isn't legendary. " +
        "You may choose new targets for the copy. (A copy of a permanent spell becomes a token.)"

    keywordAbility(KeywordAbility.ward("{3}"))

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, player = Player.You)
        effect = CopyTargetSpellEffect(
            target = EffectTarget.TriggeringEntity,
            removeLegendary = true
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "72"
        artist = "Lorenzo Lanfranconi"
        flavorText = "Isengard had once been filled with fruitful trees, but no green thing grew there in the latter days of Saruman."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52884e67-c742-4799-9afd-55bc70b2cf40.jpg?1686968322"
    }
}
