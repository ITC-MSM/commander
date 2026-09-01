package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brigid, Hero of Kinsbaile
 * {2}{W}{W}
 * Legendary Creature — Kithkin Archer
 * 2/3
 *
 * First strike
 * {T}: Brigid deals 2 damage to each attacking or blocking creature target player controls.
 *
 * The player is the only *target*; the creatures are a group gathered on resolution, so a
 * creature that joins combat after the ability was activated is still hit and one that leaves
 * simply drops out. `targetPlayerControls` binds the group's controller predicate to that
 * target rather than to Brigid's controller — pointing the ability at yourself is legal and
 * shoots your own attackers.
 *
 * `attackingOrBlocking()` is one filter carrying both state predicates as alternatives, so the
 * group holds each matching permanent exactly once and nothing takes 2 damage twice.
 *
 * Brigid is the damage source but this is not combat damage — her first strike has nothing to
 * do with it. Activated in the declare-blockers step it kills blockers before combat damage is
 * dealt, which is the whole point of the card.
 */
val BrigidHeroOfKinsbaile = card("Brigid, Hero of Kinsbaile") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Kithkin Archer"
    power = 2
    toughness = 3
    oracleText = "First strike\n" +
        "{T}: Brigid deals 2 damage to each attacking or blocking creature target player controls."

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Tap
        val player = target("target player", Targets.Player)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.attackingOrBlocking().targetPlayerControls(player)),
            Effects.DealDamage(2, EffectTarget.Self)
        )
        description = "Brigid deals 2 damage to each attacking or blocking creature target " +
            "player controls."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Steve Prescott"
        flavorText = "Thanks to one champion archer, the true borders of Kinsbaile extend an " +
            "arrow's flight beyond the buildings."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da70a20f-213e-4d79-a46f-1ef1fc3f4a51.jpg?1783942917"
    }
}
