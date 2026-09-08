package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.OnEnterRunEffect
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Molten Sentry
 * {3}{R}
 * Creature — Elemental
 * &#42;/&#42;
 * As this creature enters, flip a coin. If the coin comes up heads, this creature enters as a 5/2
 * creature with haste. If it comes up tails, this creature enters as a 2/5 creature with defender.
 *
 * There is no "enters as an X/Y with keyword" entry replacement, and there shouldn't be: a coin
 * flip is not a *choice*, so [com.wingedsheep.sdk.scripting.EntersWithChoice] is the wrong shape,
 * and the two outcomes are just a base-P/T set plus a keyword grant that already exist. So the
 * card is [OnEnterRunEffect] wrapping a [FlipCoinEffect] whose two branches each compose
 * `SetBasePowerAndToughness` + `GrantKeyword` at [Duration.Permanent] — nothing here expires, and
 * neither branch is undone if the Sentry later stops being the thing it entered as.
 *
 * The printed P/T is `*`/`*`: without the entry replacement the Sentry has no defined size, which
 * is 0/0. It never reaches state-based actions at that size, because `OnEnterRunEffect` runs
 * inline with the entry — after placement, before SBAs — exactly as Frankenstein's Monster relies
 * on to survive as an 0/1 until its counters land.
 *
 * **Known divergence, shared with Frankenstein's Monster and Nameless Race.** "As this creature
 * enters" is a true entry replacement, but `OnEnterRunEffect` runs just *after* the permanent is
 * on the battlefield. The permanent is momentarily a 0/0 Elemental with no keywords, so an ETB
 * trigger elsewhere that reads its power sees 0 rather than 5 or 2. Closing that needs a
 * pre-entry replacement hook, which is engine work rather than card work.
 */
val MoltenSentry = card("Molten Sentry") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 0
    toughness = 0
    oracleText = "As this creature enters, flip a coin. If the coin comes up heads, this creature " +
        "enters as a 5/2 creature with haste. If it comes up tails, this creature enters as a 2/5 " +
        "creature with defender."

    replacementEffect(
        OnEnterRunEffect(
            FlipCoinEffect(
                wonEffect = Effects.Composite(
                    Effects.SetBasePowerAndToughness(5, 2, EffectTarget.Self, Duration.Permanent),
                    Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self, Duration.Permanent),
                ),
                lostEffect = Effects.Composite(
                    Effects.SetBasePowerAndToughness(2, 5, EffectTarget.Self, Duration.Permanent),
                    Effects.GrantKeyword(Keyword.DEFENDER, EffectTarget.Self, Duration.Permanent),
                ),
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "Kev Walker"
        flavorText = "Some take after their father, Stone, some after their mother, Fire."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9eb6c92-b385-4ef4-83ac-65a1a23e3d22.jpg?1783943649"

        ruling(
            "2005-10-01",
            "If a creature enters copying Molten Sentry, that creature's controller flips a coin " +
                "to see what the copy will be."
        )
        ruling(
            "2005-10-01",
            "If a creature that's already on the battlefield copies Molten Sentry, it becomes a " +
                "copy of whatever Molten Sentry already is (either a 5/2 creature with haste or a " +
                "2/5 creature with defender)."
        )
    }
}
