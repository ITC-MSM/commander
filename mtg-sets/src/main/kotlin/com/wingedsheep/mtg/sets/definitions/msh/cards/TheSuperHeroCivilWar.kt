package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Super Hero Civil War — Marvel Super Heroes #231
 * {3}{R}{W} · Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Gain control of up to two target creatures with total mana value 6 or less for as long as
 *     this Saga remains on the battlefield.
 * II — Creatures you control get +1/+1 and gain vigilance until end of turn.
 * III — Target creature you control fights up to one other target creature.
 *
 * Modeling notes:
 *  - Chapter I is one targeting requirement, not two: "total mana value 6 or less" is an
 *    *aggregate* restriction across the chosen targets (CR 601.2c), so it's
 *    [TargetObject.totalManaValueAtMost] on a single `count = 2, optional = true` requirement.
 *    Two separate "mana value 6 or less" requirements would have been strictly more permissive.
 *    [ForEachTargetEffect] then fans the gain-control out over however many were chosen (zero,
 *    one, or two), so declining is a clean no-op.
 *  - Chapter I's duration is [Duration.WhileSourceOnBattlefield], not
 *    [Duration.WhileYouControlSource] (Scroll of Isildur's): the printed wording is "for as long
 *    as this Saga *remains on the battlefield*", so an opponent stealing the Saga does not hand
 *    the creatures back — only the Saga leaving does. The sacrifice after chapter III is what
 *    normally ends it, which is the whole tension of the card.
 *  - Chapter II is the Three Blind Mice IV shape — a resolution-time snapshot over
 *    [Filters.Group.creaturesYouControl], so it also pumps the creatures chapter I just stole,
 *    and a creature that arrives later in the turn misses out.
 *  - Chapter III's second target is "up to one", so an omitted target must be a clean no-op: the
 *    [ConditionalEffect] gate checks that a second positional target actually resolved before
 *    running [Effects.Fight] (whose executor errors on an unresolvable target rather than
 *    shrugging).
 *
 * Known engine deviations (both pre-existing, shared with Fall of Gil-galad / Fire Lord Sozin):
 *  - The `totalManaValueAtMost` cap is enforced interactively by `DecisionValidators` (which sums
 *    each selected permanent's mana value), but `TargetValidator`'s authoritative cross-target
 *    check only sums *card* targets, so battlefield permanents contribute 0 there — it can only
 *    ever be more permissive, never reject a legal choice. The battlefield targeting UI likewise
 *    doesn't gray out over-cap creatures the way `GraveyardTargetingUI` does; the server rejects
 *    the selection instead.
 *  - "up to one *other* target creature" means other than the *first chosen target*, which no
 *    existing target filter can express (`excludeSelf` excludes the ability's source — here the
 *    Saga, which isn't a creature). Cross-requirement duplicates are legal in the engine, so a
 *    player could point both slots at the same creature and have it fight itself. Strictly
 *    self-harming, and the same gap Fall of Gil-galad already ships with.
 */
val TheSuperHeroCivilWar = card("The Super Hero Civil War") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice " +
        "after III.)\n" +
        "I — Gain control of up to two target creatures with total mana value 6 or less for as " +
        "long as this Saga remains on the battlefield.\n" +
        "II — Creatures you control get +1/+1 and gain vigilance until end of turn.\n" +
        "III — Target creature you control fights up to one other target creature."

    // I — Gain control of up to two target creatures with total mana value 6 or less for as long
    //     as this Saga remains on the battlefield.
    sagaChapter(1) {
        target(
            "up to two target creatures with total mana value 6 or less",
            TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter.Creature,
                totalManaValueAtMost = DynamicAmount.Fixed(6),
            )
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.GainControl(
                    EffectTarget.ContextTarget(0),
                    Duration.WhileSourceOnBattlefield("this Saga"),
                )
            )
        )
    }

    // II — Creatures you control get +1/+1 and gain vigilance until end of turn.
    sagaChapter(2) {
        effect = Effects.Composite(
            Patterns.Group.modifyStatsForAll(
                power = 1,
                toughness = 1,
                filter = Filters.Group.creaturesYouControl,
            ),
            Patterns.Group.grantKeywordToAll(
                Keyword.VIGILANCE,
                Filters.Group.creaturesYouControl,
            ),
        )
    }

    // III — Target creature you control fights up to one other target creature.
    sagaChapter(3) {
        val mine = target("target creature you control", Targets.CreatureYouControl)
        val other = target("up to one other target creature", TargetCreature(optional = true))
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature, targetIndex = 1),
            effect = Effects.Fight(mine, other),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Serena Malyon"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbd1f333-5640-41f8-b9dd-1c322ac7724a.jpg?1783902897"
    }
}
