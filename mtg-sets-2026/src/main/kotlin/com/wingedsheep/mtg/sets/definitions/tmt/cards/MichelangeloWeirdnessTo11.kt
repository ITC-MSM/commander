package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyCounterPlacement

/**
 * Michelangelo, Weirdness to 11
 * {1}{G}
 * Legendary Creature — Mutant Ninja Turtle
 * 1/1
 *
 * When Michelangelo enters, create a Mutagen token.
 * If one or more +1/+1 counters would be put on a creature you control, that many
 * plus one +1/+1 counters are put on it instead.
 */
val MichelangeloWeirdnessTo11 = card("Michelangelo, Weirdness to 11") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "When Michelangelo enters, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")\nIf one or more +1/+1 counters would be put on a creature you control, that many plus one +1/+1 counters are put on it instead."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateMutagenToken()
        description = "When Michelangelo enters, create a Mutagen token."
    }

    // "that many plus one" — add one extra +1/+1 counter (Hardened Scales shape); default
    // appliesTo is +1/+1 counters placed on a creature you control.
    replacementEffect(ModifyCounterPlacement(modifier = 1))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Jason Kiantoro"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18477047-218d-4b2a-a086-37431b6a3025.jpg?1769006195"
    }
}
