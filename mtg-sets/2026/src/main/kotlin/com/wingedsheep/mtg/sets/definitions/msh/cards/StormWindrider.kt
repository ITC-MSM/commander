package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeAttackedBy
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Storm, Windrider — Marvel Super Heroes #230 (rare)
 * {1}{G}{W}{W} · Legendary Creature — Mutant Hero · 4/4
 *
 * Flying
 * Creatures with flying can't attack you or block creatures you control.
 * Whenever you cast a spell that targets one or more creatures, those creatures gain flying
 * until end of turn.
 *
 * Three clauses, three existing shapes — the middle line is one sentence but two *different*
 * combat restrictions, and each lands on the rule that owns it:
 *
 *  - **"can't attack you"** is [CantBeAttackedBy], the general defender-side restriction
 *    (`CantBeAttackedByDefenderRule`, CR 508.1c): the attacking player's declaration is checked
 *    against every permanent the *defending* player controls. Storm is the positive-filter case
 *    (`Creature.withKeyword(FLYING)`) of the same static that renders Form of the Dragon's
 *    "creatures **without** flying can't attack you". Note the scope is the player: a flier may
 *    still attack a planeswalker Storm's controller controls.
 *  - **"or block creatures you control"** is *not* a "can't block" — it names which attackers
 *    can't be blocked, which is exactly [CantBeBlockedBy] with a battlefield-scoped group filter
 *    (`Wall Crawl`'s shape): every creature you control can't be blocked by creatures with flying.
 *    A flat `CantBlock` would also stop those fliers blocking a *third* player's attackers, which
 *    the card doesn't say.
 *  - **"those creatures gain flying"** is the engine-seeded trigger capture, the same channel a
 *    batched ETB payoff reads (Kambal, Profiteering Mayor): `Triggers.youCastSpellTargeting(filter)`
 *    records exactly the targets that matched *its own* filter into
 *    [IterationSpace.TRIGGER_CAPTURED_COLLECTION], so the clause that decides *whether* it triggers
 *    and the clause that decides *what it acts on* are one computation and can't drift. Because the
 *    capture happens when the ability triggers, countering or retargeting the spell in response to
 *    the trigger doesn't change which creatures gain flying (CR 113.7a).
 *
 * The three lines feed each other: granting flying to a creature an opponent controls also takes
 * that creature out of blocking duty against your board for the turn, and off the attack against
 * you next turn if the grant were permanent (it isn't — it's until end of turn).
 */
val StormWindrider = card("Storm, Windrider") {
    manaCost = "{1}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Mutant Hero"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Creatures with flying can't attack you or block creatures you control.\n" +
        "Whenever you cast a spell that targets one or more creatures, those creatures gain " +
        "flying until end of turn."

    keywords(Keyword.FLYING)

    // "Creatures with flying can't attack you"
    staticAbility {
        ability = CantBeAttackedBy(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    // "... or block creatures you control" — read from the attacker's side: creatures you control
    // can't be blocked by creatures with flying. Storm is herself in the group.
    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    triggeredAbility {
        trigger = Triggers.youCastSpellTargeting(GameObjectFilter.Creature)
        effect = ForEachInCollectionEffect(
            collection = IterationSpace.TRIGGER_CAPTURED_COLLECTION,
            effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
        description = "Whenever you cast a spell that targets one or more creatures, those " +
            "creatures gain flying until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Immanuela Crovius"
        flavorText = "\"Wind and lightning, hear my call!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e90196a9-5a76-42f8-9b40-097d02b47f33.jpg?1783902896"
    }
}
