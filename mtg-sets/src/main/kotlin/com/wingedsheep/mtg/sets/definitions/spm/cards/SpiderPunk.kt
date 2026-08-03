package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.RIOT_REMINDER
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.dsl.riotFor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DamageCantBePrevented
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCantBeCountered

/**
 * Spider-Punk
 * {1}{R}
 * Legendary Creature — Spider Human Hero
 * 2/1
 *
 * Riot (This creature enters with your choice of a +1/+1 counter or haste.)
 * Other Spiders you control have riot.
 * Spells and abilities can't be countered.
 * Damage can't be prevented.
 *
 * Modelling notes:
 * - **Riot** (CR 702.136) is the `EntersWithRiot` replacement, added by the `riot()` DSL helper;
 *   `riotFor(…)` adds the `otherOnly` instance that reaches every *other* Spider you control as it
 *   enters. Each instance is answered separately (CR 702.136b), so a Spider that picks up riot from
 *   two sources makes two choices and can end up with a counter *and* haste. Both are replacement
 *   effects: the choice can't be responded to, and the haste branch is indefinite rather than
 *   until end of turn. A creature that can't have +1/+1 counters put on it isn't offered the
 *   counter branch at all and simply gains haste.
 * - **"Spells and abilities can't be countered"** is one [GrantCantBeCountered] over
 *   [GameObjectFilter.Any] with `includesAbilities = true`. It protects *every* player's spells and
 *   abilities, not just this card's controller's. Per the printed ruling a counterspell may still
 *   target them — it just counters nothing when it resolves.
 * - **"Damage can't be prevented"** is the existing global [DamageCantBePrevented] replacement
 *   (Sunspine Lynx, Leyline of Punishment), consulted from the battlefield by every damage path.
 */
val SpiderPunk = card("Spider-Punk") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Spider Human Hero"
    power = 2
    toughness = 1
    oracleText = "$RIOT_REMINDER\n" +
        "Other Spiders you control have riot.\n" +
        "Spells and abilities can't be countered.\n" +
        "Damage can't be prevented."

    riot()
    riotFor(GameObjectFilter.Creature.withSubtype(Subtype.SPIDER).youControl())

    staticAbility {
        ability = GrantCantBeCountered(
            filter = GameObjectFilter.Any,
            includesAbilities = true,
        )
    }

    replacementEffect(DamageCantBePrevented())

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "Forrest Imel"
        flavorText = "\"Disrespect authority. Smash the system.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bd41879-fcd4-4211-9b98-47e7cdba5399.jpg?1783905333"

        ruling(
            "2025-09-19",
            "While Spider-Punk is on the battlefield, spells and abilities can still be the target " +
                "of another spell or ability that would normally counter them, but they won't be " +
                "countered as that spell or ability resolves."
        )
        ruling(
            "2025-09-19",
            "Riot is a replacement effect. Players can't respond to your choice of a +1/+1 counter " +
                "or haste."
        )
        ruling(
            "2025-09-19",
            "If you choose for a creature with riot to gain haste, it gains haste indefinitely. It " +
                "doesn't lose it as the turn ends or if another player gains control of it."
        )
        ruling(
            "2025-09-19",
            "If a creature entering the battlefield has riot but can't have +1/+1 counters put on " +
                "it, it gains haste."
        )
    }
}
