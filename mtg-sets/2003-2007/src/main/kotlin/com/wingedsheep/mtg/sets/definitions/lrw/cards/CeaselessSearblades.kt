package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ceaseless Searblades
 * {3}{R}
 * Creature — Elemental Warrior
 * 2/4
 * Whenever you activate an ability of an Elemental, this creature gets +1/+0 until end of turn.
 *
 * Three readings in this one line are each load-bearing, and each one is a place the obvious
 * spelling would be wrong:
 *
 * - **"an ability" is unqualified**, so mana abilities count — `includeManaAbilities = true`, the
 *   Elrond, Moon-Reader wording, not the default "that isn't a mana ability". A Smokebraider's
 *   "{T}: Add two mana of any one color, spend this mana only to cast Elemental spells" pumps the
 *   Searblades. The pump itself can't produce mana, so this trigger is not itself a mana ability
 *   (CR 605.1b) and uses the stack normally.
 * - **"an Elemental" is the bare tribal noun**, which means an Elemental *permanent*, not an
 *   Elemental creature. Lorwyn's Kindred noncreature Elementals count: activating Hoofprints of
 *   the Stag triggers this.
 * - **`.onBattlefield()` is not decoration.** The 2007-10-01 ruling says this triggers only for an
 *   Elemental *permanent*, never for an Elemental source outside the battlefield — a card whose
 *   ability is activated from hand or graveyard. `GameObjectFilter`'s type predicates read a
 *   card's printed types in any zone, so without the state predicate a graveyard-activated
 *   Elemental would wrongly fire it.
 *
 * The trigger is unrestricted by whose Elemental it is — "you activate", not "you control": an
 * ability you activate on an Elemental you don't control still counts.
 */
val CeaselessSearblades = card("Ceaseless Searblades") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Warrior"
    power = 2
    toughness = 4
    oracleText = "Whenever you activate an ability of an Elemental, this creature gets +1/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.activatesAbilityOf(
            GameObjectFilter.Permanent.withSubtype(Subtype.ELEMENTAL).onBattlefield(),
            includeManaAbilities = true
        )
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "Whenever you activate an ability of an Elemental, this creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Jim Murray"
        flavorText = "Flamekins' fires burn cool until they decide otherwise."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7e103a3-555c-4b68-9768-42637bfd2478.jpg?1783942878"
        ruling(
            "2007-10-01",
            "This triggers whenever you activate an activated ability of an Elemental permanent, " +
                "but not when you activate an activated ability of an Elemental source that's not on the battlefield."
        )
    }
}
