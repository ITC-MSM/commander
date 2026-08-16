package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nulldrifter
 * {7}
 * Creature — Eldrazi Elemental
 * 4/4
 *
 * When you cast this spell, draw two cards.
 * Flying
 * Annihilator 1
 * Evoke {2}{U}
 *
 * Modeling notes:
 *  - The draw is a **cast trigger** ([Triggers.WhenYouCastThisSpell]), not an enters trigger. That
 *    is what makes the evoke line work: evoking still *casts* the spell, so the trigger fires and
 *    resolves before Nulldrifter itself does, and the two cards are drawn even though the body is
 *    sacrificed the moment it enters. It also means the draw survives Nulldrifter being countered
 *    (per ruling), because the trigger is a separate object on the stack.
 *  - `evoke` is the first-class alt-cost field on the card DSL; the engine offers it as a second
 *    cast option and schedules the sacrifice-on-ETB itself, so nothing else is authored for it.
 *  - Annihilator is a display-only [KeywordAbility.Numeric] in the SDK (like rampage / bushido), so
 *    the behaviour is lowered here as the triggered ability the keyword abbreviates: on attack, the
 *    defending player sacrifices a permanent of their choice. The edict form
 *    ([Effects.Sacrifice]) is the right primitive — the *defending player* chooses, and the filter
 *    is [GameObjectFilter.Permanent] rather than `Creature`, since annihilator eats any permanent.
 */
val Nulldrifter = card("Nulldrifter") {
    manaCost = "{7}"
    colorIdentity = "U"
    typeLine = "Creature — Eldrazi Elemental"
    power = 4
    toughness = 4
    oracleText = "When you cast this spell, draw two cards.\n" +
        "Flying\n" +
        "Annihilator 1 (Whenever this creature attacks, defending player sacrifices a permanent " +
        "of their choice.)\n" +
        "Evoke {2}{U} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.annihilator(1))

    evoke = "{2}{U}"

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.DrawCards(2)
        description = "When you cast this spell, draw two cards."
    }

    // Annihilator 1 — the lowering of the display-only keyword ability above.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Sacrifice(
            GameObjectFilter.Permanent,
            1,
            EffectTarget.PlayerRef(Player.DefendingPlayer)
        )
        description = "Annihilator 1"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "13"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d9f1bb8-c91b-40cd-a416-abbff0d65306.jpg?1783911308"

        ruling(
            "2024-06-07",
            "Nulldrifter's first triggered ability will resolve before Nulldrifter does. If " +
                "Nulldrifter is countered or otherwise leaves the stack in response to that " +
                "triggered ability, the triggered ability will still resolve as normal."
        )
        ruling(
            "2024-06-07",
            "To determine the total cost of a spell, start with the mana cost or alternative " +
                "cost you're paying (such as an evoke cost), add any cost increases, then apply " +
                "any cost reductions. The mana value of the spell is determined by only its mana " +
                "cost, no matter what the total cost to cast that spell was."
        )
        ruling(
            "2024-06-07",
            "Annihilator abilities trigger and resolve during the declare attackers step. The " +
                "defending player sacrifices the required number of permanents of their choice " +
                "before they declare blockers. Any creatures sacrificed this way won't be able " +
                "to block."
        )
        ruling(
            "2024-06-07",
            "If a creature with annihilator is attacking a planeswalker, and the defending " +
                "player chooses to sacrifice that planeswalker, the attacking creature continues " +
                "to attack. It may be blocked. If it isn't blocked, it simply won't deal combat " +
                "damage to anything."
        )
    }
}
