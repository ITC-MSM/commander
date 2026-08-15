package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Earth Rumble
 * {3}{G}
 * Sorcery
 *
 * Earthbend 2. When you do, up to one target creature you control fights target
 * creature an opponent controls. (To earthbend 2, target land you control becomes a
 * 0/0 creature with haste that's still a land. Put two +1/+1 counters on it. When it
 * dies or is exiled, return it to the battlefield tapped. Creatures that fight each
 * deal damage equal to their power to the other.)
 *
 * Modeling notes:
 *  - Earthbend is a keyword *action* composed from primitives via [Effects.Earthbend]
 *    (animate the target land, grant haste, add two +1/+1 counters, grant the
 *    return-tapped self-trigger) — same as Rockalanche / Earthbender Ascension. Its land
 *    is a true target chosen as the sorcery is cast.
 *  - "When you do" is the reflexive trigger off the (mandatory) earthbend action, modeled
 *    with [ReflexiveTriggerEffect] (`optional = false`): the earthbend resolves first, then
 *    the fight goes on the stack with its own targets chosen at that point (SandbenderScavengers
 *    / Gimli shape). "up to one target creature you control" is the optional first reflexive
 *    target; "target creature an opponent controls" is the mandatory second. A symmetric
 *    [Effects.Fight] reads them as `ContextTarget(0)`/`ContextTarget(1)`; if the optional
 *    fighter is declined, no creature is dealt damage.
 */
val EarthRumble = card("Earth Rumble") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Earthbend 2. When you do, up to one target creature you control fights target creature an opponent controls. " +
        "(To earthbend 2, target land you control becomes a 0/0 creature with haste that's still a land. " +
        "Put two +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped. " +
        "Creatures that fight each deal damage equal to their power to the other.)"

    spell {
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = ReflexiveTriggerEffect(
            action = Effects.Earthbend(2, land),
            optional = false,
            reflexiveEffect = Effects.Fight(EffectTarget.ContextTarget(0), EffectTarget.ContextTarget(1)),
            reflexiveTargetRequirements = listOf(
                TargetCreature(count = 1, optional = true, filter = TargetFilter.CreatureYouControl),
                TargetCreature(filter = TargetFilter.CreatureOpponentControls)
            ),
            descriptionOverride = "Earthbend 2. When you do, up to one target creature you control " +
                "fights target creature an opponent controls."
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62730505-56f1-4043-a8b9-4fb7bc508b47.jpg?1764121183"
    }
}
