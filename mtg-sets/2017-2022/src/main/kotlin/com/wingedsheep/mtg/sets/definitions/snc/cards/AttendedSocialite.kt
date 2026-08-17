package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Attended Socialite
 * {1}{G}
 * Creature — Elf Druid
 * 2 / 1
 * Alliance — Whenever another creature you control enters, this creature gets +1/+1 until end of turn.
 *
 * "Alliance" is a pure ability word — no rules meaning — so the trigger is the plain
 * [Triggers.OtherCreatureEnters] (OTHER binding over creatures you control), with the ability word
 * carried only in the printed text and the [description].
 */
val AttendedSocialite = card("Attended Socialite") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Alliance — Whenever another creature you control enters, this creature gets +1/+1 until end of turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Alliance — Whenever another creature you control enters, this creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Drew Baker"
        flavorText = "\"Say hello to my little friend.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bccccf9-3ee5-4485-ae01-4dcbad989d18.jpg?1783923110"
    }
}
