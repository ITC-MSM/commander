package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * View from Above
 * {1}{U}
 * Instant
 * Target creature gains flying until end of turn. If you control a white permanent, return
 * View from Above to its owner's hand.
 *
 * Two sentences chained with `then`. The rider is a [Gate.WhenCondition] — a state check taken
 * when View from Above *resolves*, not an intervening-if, so a white permanent that arrives after
 * the spell was cast still counts. The condition is the general
 * [Exists] over your battlefield with a bare "white permanent" filter (`IsPermanent` +
 * `HasColor(WHITE)`), and the return moves [EffectTarget.Self] — the spell itself, which is still
 * on the stack — to [Zone.HAND] rather than the targeted creature.
 */
val ViewFromAbove = card("View from Above") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gains flying until end of turn. If you control a white permanent, " +
        "return View from Above to its owner's hand."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
            .then(
                GatedEffect(
                    gate = Gate.WhenCondition(
                        Exists(
                            Player.You,
                            Zone.BATTLEFIELD,
                            GameObjectFilter.Permanent.withColor(Color.WHITE)
                        )
                    ),
                    then = Effects.ReturnToHand(EffectTarget.Self)
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Howard Lyon"
        flavorText = "\"This air feels so heavy and thick. There are no winds to speak of. I fear the knowledge that comes from a place like this.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dc73034-4886-4855-b6de-392fa053fe9e.jpg"
    }
}
