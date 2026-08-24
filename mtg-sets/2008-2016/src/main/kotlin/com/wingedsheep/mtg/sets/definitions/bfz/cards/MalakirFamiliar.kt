package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Malakir Familiar
 * {2}{B}
 * Creature — Bat
 * 2/1
 * Flying, deathtouch
 * Whenever you gain life, this creature gets +1/+1 until end of turn.
 */
val MalakirFamiliar = card("Malakir Familiar") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    power = 2
    toughness = 1
    oracleText = "Flying, deathtouch\n" +
        "Whenever you gain life, this creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "116"
        artist = "Alejandro Mirabal"
        flavorText = "\"They are deadly, and they are loyal. We can spare them a little blood.\"\n" +
            "—Harak, Malakir bloodwitch"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f03b00a-f6d0-419a-abfd-fa8bc63226db.jpg?1783938200"
    }
}
