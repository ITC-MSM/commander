package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Kami of Fire's Roar
 * {3}{R}
 * Creature — Spirit
 * 2/3
 * Whenever you cast a Spirit or Arcane spell, target creature can't block this turn.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Kami of Fire's Roar also triggers off its own cast.
 *
 * "Can't block this turn" is `Effects.CantBlock`, whose default `Duration.EndOfTurn` already says
 * "this turn"; it is a combat-legality rider on the target, not a projection change.
 */
val KamiOfFiresRoar = card("Kami of Fire's Roar") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, target creature can't block this turn."
    power = 2
    toughness = 3
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.CantBlock(t)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Dave Dorman"
        flavorText = "\"I can hear the shamans chanting in the hills. They say their magic will protect us from the kami, that our gold has bought our safety. But no one sleeps soundly tonight.\"\n—Scroll fragment from the ruins of Reito"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4380118d-f209-446d-829b-3564796e0219.jpg?1783944299"
    }
}
