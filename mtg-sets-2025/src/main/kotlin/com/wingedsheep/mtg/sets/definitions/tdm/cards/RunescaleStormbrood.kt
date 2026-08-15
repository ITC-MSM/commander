package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Runescale Stormbrood // Chilling Screech — Tarkir: Dragonstorm #221
 * {3}{R} · Creature — Dragon · 2/4
 *
 * Flying
 * Whenever you cast a noncreature spell or a Dragon spell, this creature gets +2/+0 until end of turn.
 *
 * Omen: Chilling Screech — {1}{U}, Instant — Omen
 * Counter target spell with mana value 2 or less.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val RunescaleStormbrood = card("Runescale Stormbrood") {
    manaCost = "{3}{R}"
    colorIdentity = "RU"
    typeLine = "Creature — Dragon"
    power = 2
    toughness = 4
    oracleText = "Flying\nWhenever you cast a noncreature spell or a Dragon spell, " +
        "this creature gets +2/+0 until end of turn."

    keywords(Keyword.FLYING)

    // Whenever you cast a noncreature spell OR a Dragon spell, +2/+0 until end of turn.
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Noncreature or GameObjectFilter.Any.withSubtype(Subtype.DRAGON)
        )
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    // Omen: Chilling Screech — Instant. Counter target spell with mana value 2 or less.
    omen("Chilling Screech") {
        manaCost = "{1}{U}"
        typeLine = "Instant — Omen"
        oracleText = "Counter target spell with mana value 2 or less. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            target("spell with mana value 2 or less", Targets.SpellWithManaValueAtMost(2))
            effect = Effects.CounterSpell()
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/317744d1-ed78-4b53-a4d8-8c7ecfd9c4ae.jpg?1743204873"
    }
}
