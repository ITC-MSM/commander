package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Kami of the Waning Moon
 * {2}{B}
 * Creature — Spirit
 * 1/1
 * Flying
 * Whenever you cast a Spirit or Arcane spell, target creature gains fear until end of turn.
 *
 * The Kamigawa block's signature payoff trigger. "Whenever you cast a Spirit or Arcane spell" is a
 * [Triggers.youCastSpell] over a homogeneous OR of two subtype filters, which the SDK collapses to
 * a single `CardPredicate.Or` — the flat shape the whole engine already resolves subtypes against.
 * The binding is `ANY` (the factory's default), not `OTHER`: the card watches every Spirit or
 * Arcane spell *you* cast, including the one that is this card itself when a later copy is cast
 * while this one is already on the battlefield.
 *
 * "Target creature" is chosen when the trigger goes on the stack, and fear is a plain
 * end-of-turn [Effects.GrantKeyword] — the same channel Gravelgill Duo and Breach use.
 */
val KamiOfTheWaningMoon = card("Kami of the Waning Moon") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "Whenever you cast a Spirit or Arcane spell, target creature gains fear until end of " +
        "turn. (It can't be blocked except by artifact creatures and/or black creatures.)"
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FEAR, creature)
        description = "Whenever you cast a Spirit or Arcane spell, target creature gains fear " +
            "until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Matt Thompson"
        flavorText = "\"For a moment, Reito's defenders regrouped. Then wailing kami reappeared " +
            "to send them scattering like flocks of frightened birds.\"\n" +
            "—*Great Battles of Kamigawa*"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23a395a6-b1f5-4b7f-87cc-c77b4d497e9a.jpg?1783944313"
    }
}
