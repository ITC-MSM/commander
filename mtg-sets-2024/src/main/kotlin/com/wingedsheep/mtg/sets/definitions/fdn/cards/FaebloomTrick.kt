package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Faebloom Trick
 * {2}{U}
 * Instant
 *
 * Create two 1/1 blue Faerie creature tokens with flying. When you do, tap target creature an
 * opponent controls.
 *
 * The tap is a reflexive "when you do" trigger: the tokens are always created, and the reflexive
 * trigger chooses its target afterward — so casting this with no opponent creatures still makes the
 * tokens and simply has no legal tap target. Modeled with [ReflexiveTriggerEffect] (mandatory
 * action), not a cast-time target.
 */
val FaebloomTrick = card("Faebloom Trick") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Create two 1/1 blue Faerie creature tokens with flying. When you do, tap target " +
        "creature an opponent controls."

    spell {
        effect = ReflexiveTriggerEffect(
            action = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Faerie"),
                keywords = setOf(Keyword.FLYING),
                count = 2,
                imageUri = "https://cards.scryfall.io/normal/front/d/1/d1c0556e-ba3c-4a8e-b704-8eaa7c4dba1c.jpg?1782727481"
            ),
            optional = false,
            reflexiveEffect = Effects.Tap(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureOpponentControls)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Annie Stegg"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c3bee8f-f5be-4404-a696-c902637799c3.jpg?1782689233"
    }
}
