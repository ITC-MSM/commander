package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Town Sentry
 * {2}{W}
 * Creature — Human Soldier
 * 2/2
 * Whenever this creature blocks, it gets +0/+2 until end of turn.
 *
 * [Triggers.Blocks] is the SELF-bound "this creature blocks" event, so the pump aims at
 * [EffectTarget.Self] — Shu Defender in white.
 */
val TownSentry = card("Town Sentry") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Whenever this creature blocks, it gets +0/+2 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ModifyStats(0, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Bradley Williams"
        flavorText = "\"So he gots a sword. Big deal.\"\n\"Yeah, it's a big deal—it's a big sword!\"\n—Two goblins outside the gates of Trokin"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5e9db36-0592-4e59-951a-d9d6c1522b99.jpg"
    }
}
