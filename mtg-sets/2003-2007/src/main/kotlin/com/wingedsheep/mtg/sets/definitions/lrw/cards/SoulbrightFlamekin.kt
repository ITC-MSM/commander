package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Soulbright Flamekin — Lorwyn #190
 * {1}{R} · Creature — Elemental Shaman · 2/1
 *
 * {2}: Target creature gains trample until end of turn. If this is the third time this ability
 * has resolved this turn, you may add {R}{R}{R}{R}{R}{R}{R}{R}.
 *
 * Shares its whole shape with [InnerFlameIgniter]: pay off the printed clause, bump the source's
 * per-turn resolution tally with [IncrementAbilityResolutionCountEffect], then gate the rider on
 * [Conditions.SourceAbilityResolvedNTimes] reading that tally back. The equality (not a
 * threshold) is what makes the ruling "you won't get the bonus the fourth, fifth, sixth …" fall
 * out for free.
 *
 * "**You may** add" is a real decision, not a selection that can be declined, so the mana sits
 * under [MayEffect] — eight red mana that must be spent this step is often a liability, and CR
 * 106.4 wants the player asked.
 *
 * The ability **targets**, so it is not a mana ability (its own ruling says so, CR 605.1a): it
 * uses the stack and can be responded to. Nothing on the card says that — the engine derives
 * `manaAbility` from the presence of a target requirement, so declaring the target is the whole
 * of it.
 */
val SoulbrightFlamekin = card("Soulbright Flamekin") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 2
    toughness = 1
    oracleText = "{2}: Target creature gains trample until end of turn. If this is the third time " +
        "this ability has resolved this turn, you may add {R}{R}{R}{R}{R}{R}{R}{R}."

    activatedAbility {
        cost = Costs.Mana("{2}")
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, creature, Duration.EndOfTurn)
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(3),
                    effect = MayEffect(
                        effect = Effects.AddMana(Color.RED, 8),
                        hint = "Add {R}{R}{R}{R}{R}{R}{R}{R}?"
                    )
                )
            )
        description = "Target creature gains trample until end of turn. If this is the third time " +
            "this ability has resolved this turn, you may add {R}{R}{R}{R}{R}{R}{R}{R}."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Kev Walker"
        flavorText = "When provoked, a flamekin's inner fire burns far hotter than any giant's forge."
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaf72b4d-a399-49fb-a890-653b184a9e95.jpg?1783942871"
        ruling("2018-03-16", "You add eight red mana only the third time Soulbright Flamekin's ability resolves in a turn. You won't get the bonus the fourth, fifth, sixth, or any subsequent times in that same turn.")
        ruling("2018-03-16", "Soulbright Flamekin's ability uses the stack and can be responded to. Because it has a target, it's not a mana ability, even if you know that it will produce mana.")
    }
}
