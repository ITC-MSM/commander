package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ExtraOnceOnlyActivations
import com.wingedsheep.sdk.scripting.OnceOnlyAbilityKind
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wonder Man, Hollywood Hero — Marvel Super Heroes #160 (uncommon)
 * {3}{R}{R} · Legendary Creature — Human Performer Hero · 4/4
 *
 * Flying
 * Each power-up ability of permanents you control can be activated an additional time.
 * Power-up — {5}{R}{R}: Put two +1/+1 counters on Wonder Man. (Activate each power-up ability only
 * . . . once? Reduce the cost by his mana cost if he entered this turn.)
 *
 * The reminder text is the card's own joke — Wonder Man is an actor, so his printing reads
 * "only . . . once?" where the rest of the cycle reads "only once." It is reproduced verbatim from
 * Scryfall Oracle; do not normalize it.
 *
 * The set's only card that *raises* a power-up limit instead of discounting it, and the reason
 * [ExtraOnceOnlyActivations] carries an `extraActivations` count at all. Power-up's "Activate this
 * ability only once" (CR 702.193a) is an [com.wingedsheep.sdk.scripting.ActivationRestriction.Once]
 * the `isPowerUp` flag installs, tracked per object; Wonder Man's static lifts the ceiling for
 * every power-up ability of every permanent its controller controls, his own included, from one
 * activation to two.
 *
 * Three consequences worth stating, because they follow from the permission being *continuous*
 * rather than a one-shot grant:
 *  - It stacks. Two Wonder Men mean each power-up ability may be activated three times, because the
 *    allowance sums across the battlefield.
 *  - It is checked at activation time, not banked. Spending the extra activation and then losing
 *    Wonder Man is fine, but the second activation must happen while he is out.
 *  - It never reaches an exhaust ability, or a `Once` an ordinary ability printed for itself — the
 *    `kind` axis is what keeps those separate.
 *
 * It does not beat Kang the Conqueror's "During that turn, power-up abilities can't be activated":
 * that lockout is a flat prohibition checked ahead of the ability's own restrictions, so a raised
 * limit has nothing to raise. Prohibitions beat permissions, the ordinary CR 101.2 answer.
 *
 * His own power-up is the cycle's standard shape: `{5}{R}{R}` − `{3}{R}{R}` = `{2}` the turn he
 * lands, so the intended line is cast-then-power-up for seven mana and a 6/6 flier.
 */
val WonderManHollywoodHero = card("Wonder Man, Hollywood Hero") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Performer Hero"
    oracleText = "Flying\n" +
        "Each power-up ability of permanents you control can be activated an additional time.\n" +
        "Power-up — {5}{R}{R}: Put two +1/+1 counters on Wonder Man. (Activate each power-up " +
        "ability only . . . once? Reduce the cost by his mana cost if he entered this turn.)"
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ExtraOnceOnlyActivations(
            kind = OnceOnlyAbilityKind.POWER_UP,
            extraActivations = 1
        )
    }

    activatedAbility {
        isPowerUp = true
        cost = Costs.Mana("{5}{R}{R}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Nanna Marie Steffensen"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ee52395-9523-4da1-9108-8ab79229fc7a.jpg?1783902922"
    }
}
