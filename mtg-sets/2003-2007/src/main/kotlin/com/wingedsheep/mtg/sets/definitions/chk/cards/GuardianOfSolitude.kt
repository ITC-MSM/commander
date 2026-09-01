package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Guardian of Solitude
 * {1}{U}
 * Creature — Spirit
 * 1/2
 * Whenever you cast a Spirit or Arcane spell, target creature gains flying until end of turn.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Guardian of Solitude also triggers off its own cast.
 */
val GuardianOfSolitude = card("Guardian of Solitude") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, target creature gains flying until end of turn."
    power = 1
    toughness = 2
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Stephen Tappin"
        flavorText = "\"It seemed an easy thing, to step into the nothingness, to fall, to die. But then, for an instant, I saw it, eyes filled with endless sorrow, and I turned back to face my pain.\"\n—Snow-Fur, kitsune poet"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85d16011-956b-40ac-afb6-6c7ad774802f.jpg?1783944327"
    }
}
