package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Coordinated Assault
 * {R}
 * Instant
 *
 * Up to two target creatures each get +1/+0 and gain first strike until end of turn. (They deal combat damage before creatures without first strike.)
 *
 * "Up to two target creatures **each**" is one requirement with `count = 2, optional = true`, and the
 * pump body runs once per chosen target via [IterationSpace.Targets] — so one, two, or zero targets
 * all resolve correctly and a target that became illegal is simply skipped.
 */
val CoordinatedAssault = card("Coordinated Assault") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +1/+0 and gain first strike until end of turn. (They deal combat damage before creatures without first strike.)"

    spell {
        target = TargetCreature(count = 2, optional = true)
        effect = ForEachEffect(
            space = IterationSpace.Targets,
            body = Effects.Composite(
                Effects.ModifyStats(1, 0),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "116"
        artist = "John Severin Brassell"
        flavorText = "It's hard to shout \"Shields up!\" with a javelin in your chest."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c29f484-5f4b-4d29-846f-192425ab7fe3.jpg"
    }
}
