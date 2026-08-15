package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetOther
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Appa, Steadfast Guardian — {2}{W}{W} Legendary Creature — Bison Ally — 3/4
 *
 * Flash
 * Flying
 * When Appa enters, airbend any number of other target nonland permanents you control. (Exile them.
 * While each one is exiled, its owner may cast it for {2} rather than its mana cost.)
 * Whenever you cast a spell from exile, create a 1/1 white Ally creature token.
 *
 * "Any number of other target nonland permanents you control" is an `unlimited` [TargetPermanent]
 * (you-control filter) wrapped in [TargetOther] (excludes Appa); target-agnostic [Effects.Airbend]
 * airbends every chosen permanent via `CardSource.ChosenTargets`. The cast-from-exile payoff reuses
 * the `youCastSpell` trigger gated by `SpellCastPredicate.CastFromZone(Zone.EXILE)` (Fire Lord Zuko
 * pattern) — and naturally rewards casting the very permanents Appa airbended back out.
 */
val AppaSteadfastGuardian = card("Appa, Steadfast Guardian") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Bison Ally"
    oracleText = "Flash\n" +
        "Flying\n" +
        "When Appa enters, airbend any number of other target nonland permanents you control. (Exile them. While each one is exiled, its owner may cast it for {2} rather than its mana cost.)\n" +
        "Whenever you cast a spell from exile, create a 1/1 white Ally creature token."
    power = 3
    toughness = 4

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "any number of other target nonland permanents you control",
            TargetOther(baseRequirement = TargetPermanent(unlimited = true, filter = TargetFilter.NonlandPermanent.youControl()))
        )
        effect = Effects.Airbend()
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(requires = setOf(SpellCastPredicate.CastFromZone(Zone.EXILE)))
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
        description = "Whenever you cast a spell from exile, create a 1/1 white Ally creature token."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "10"
        artist = "Maël Ollivier-Henry"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/829d91e9-4878-4e55-a262-ac0d55b65d4e.jpg?1764119935"
    }
}
