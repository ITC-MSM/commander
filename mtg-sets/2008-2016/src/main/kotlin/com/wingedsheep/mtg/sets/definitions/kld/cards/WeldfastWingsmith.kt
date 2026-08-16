package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Weldfast Wingsmith
 * {3}{U}
 * Creature — Human Artificer
 * 3/3
 * Whenever an artifact you control enters, this creature gains flying until end of turn.
 *
 * The Tidus, Blitzball Star trigger shape — [Triggers.entersBattlefield] over
 * `Artifact.youControl()` with [TriggerBinding.ANY] — feeding a self-targeted
 * [Effects.GrantKeyword] (default `Duration.EndOfTurn`).
 */
val WeldfastWingsmith = card("Weldfast Wingsmith") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Artificer"
    power = 3
    toughness = 3
    oracleText = "Whenever an artifact you control enters, this creature gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        description = "This creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Dan Murayama Scott"
        flavorText = "\"Airships are too confining. If I'm in the sky, I want to feel the wind in my hair and taste the aether.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b115a65-e273-4264-9db7-317e1855f492.jpg?1783937211"
    }
}
