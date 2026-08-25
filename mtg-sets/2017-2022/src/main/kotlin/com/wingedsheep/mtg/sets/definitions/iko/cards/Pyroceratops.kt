package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pyroceratops
 * {3}{R}
 * Creature — Elemental Dinosaur
 * 2/3
 * Trample
 * Whenever you cast a noncreature spell, put a +1/+1 counter on this creature.
 *
 * The same prowess-shaped payoff as Sprite Dragon, on a trampler: [Triggers.YouCastNoncreature]
 * is the `Player.You` + noncreature-filtered cast watcher, and the growth is a permanent
 * [Effects.AddCounters] on itself rather than an until-end-of-turn pump — so every counter it
 * earns keeps pushing damage past a chump blocker.
 */
val Pyroceratops = card("Pyroceratops") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Dinosaur"
    power = 2
    toughness = 3
    oracleText = "Trample\nWhenever you cast a noncreature spell, put a +1/+1 counter on this creature."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Jason A. Engle"
        flavorText = "\"I always thought wizards were supposed to have owls or one-eyed cats for familiars, but flaming dinosaurs work, too.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6599645-dbb5-4174-bd26-8556af6d89c3.jpg"
    }
}
