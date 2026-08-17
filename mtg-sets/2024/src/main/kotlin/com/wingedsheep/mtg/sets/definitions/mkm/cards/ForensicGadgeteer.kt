package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ReduceActivatedAbilityCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Forensic Gadgeteer — Murders at Karlov Manor #57
 * {2}{U} · Creature — Vedalken Artificer Detective · 2/3
 *
 * Whenever you cast an artifact spell, investigate.
 * Activated abilities of artifacts you control cost {1} less to activate. This effect can't reduce
 * the mana in that cost to less than one mana.
 *
 * Both halves are existing vocabulary. The trigger is `youCastSpell` filtered to artifact spells —
 * the *spell* filter, not a battlefield one, so an artifact creature spell counts too (its type line
 * on the stack is still Artifact Creature).
 *
 * The static is [ReduceActivatedAbilityCost] with `manaFloor = 1`, which is the whole second
 * sentence: the reduction only eats generic mana and stops once one mana is left, so a Clue's
 * `{2}, Sacrifice this token` becomes `{1}` and never free. Two rulings shape what the filter must
 * *not* say. It's `GameObjectFilter.Artifact.youControl()` scoped to the battlefield group, because
 * the ability reaches only artifacts you control in play — cycling and other abilities that
 * function from hand or graveyard are untouched. And triggered abilities aren't activated
 * abilities, so nothing here needs to exclude them; `ReduceActivatedAbilityCost` only ever sees the
 * activated rail.
 */
val ForensicGadgeteer = card("Forensic Gadgeteer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Artificer Detective"
    power = 2
    toughness = 3
    oracleText = "Whenever you cast an artifact spell, investigate. (Create a Clue token. It's an " +
        "artifact with \"{2}, Sacrifice this token: Draw a card.\")\n" +
        "Activated abilities of artifacts you control cost {1} less to activate. This effect can't " +
        "reduce the mana in that cost to less than one mana."

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Artifact)
        effect = Effects.Investigate()
        description = "Whenever you cast an artifact spell, investigate."
    }

    staticAbility {
        ability = ReduceActivatedAbilityCost(
            filter = GroupFilter(GameObjectFilter.Artifact.youControl()),
            amount = DynamicAmount.Fixed(1),
            manaFloor = 1
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "Volkan Baǵa"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97d08a15-e61c-4421-a541-c68a4f87cb74.jpg?1783912910"
        ruling(
            "2024-02-02",
            "Activated abilities contain a colon. They're generally written \"[Cost]: [Effect].\" " +
                "Triggered abilities (starting with \"when,\" \"whenever,\" or \"at\") are unaffected by " +
                "the cost reduction ability of Forensic Gadgeteer."
        )
        ruling(
            "2024-02-02",
            "Forensic Gadgeteer's last ability affects only abilities of artifacts you control on " +
                "the battlefield. The costs of activated abilities of artifact cards that work in other " +
                "zones, such as cycling, won't be reduced."
        )
    }
}
