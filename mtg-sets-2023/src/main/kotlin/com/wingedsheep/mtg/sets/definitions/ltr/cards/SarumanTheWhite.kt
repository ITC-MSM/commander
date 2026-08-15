package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Saruman the White
 * {4}{U}
 * Legendary Creature — Avatar Wizard
 * 4/4
 *
 * Ward {2}
 * Whenever you cast your second spell each turn, amass Orcs 2.
 */
val SarumanTheWhite = card("Saruman the White") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Avatar Wizard"
    power = 4
    toughness = 4
    oracleText = "Ward {2}\n" +
        "Whenever you cast your second spell each turn, amass Orcs 2. (Put two +1/+1 counters on an Army " +
        "you control. It's also an Orc. If you don't control an Army, create a 0/0 black Orc Army creature token first.)"

    keywordAbility(KeywordAbility.ward("{2}"))

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.Amass(2, "Orc")
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "67"
        artist = "Matt Stewart"
        flavorText = "\"Sauron's victory is at hand; and there will be rich reward for those who aided it.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bfccbab-29fa-4e92-8919-6cd4815fb655.jpg?1686968266"
    }
}
