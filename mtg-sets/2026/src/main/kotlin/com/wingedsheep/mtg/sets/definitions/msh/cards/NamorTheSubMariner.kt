package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Namor the Sub-Mariner — Marvel Super Heroes #69
 * {1}{U}{U} · Legendary Creature — Mutant Merfolk Villain · Mythic
 *
 * Flying
 * Namor's power is equal to the number of Merfolk you control.
 * Whenever you cast a noncreature spell with one or more blue mana symbols in its mana cost,
 * create that many 1/1 blue Merfolk creature tokens.
 *
 * **Power** is a characteristic-defining ability (printed `*`/4), so only power is dynamic:
 * `dynamicPower` over the count of Merfolk on the battlefield under your control. Namor is himself
 * a Merfolk, so he counts himself — an alone-on-an-empty-board Namor is 1/4. The filter is
 * `Any.withSubtype(MERFOLK)` rather than `Creature.withSubtype(...)` because the card says "the
 * number of Merfolk you control", not "Merfolk creatures"; a noncreature permanent that has the
 * Merfolk subtype counts. Counting runs over projected subtypes, so Changeling and type-changing
 * effects are picked up.
 *
 * **The trigger** is the reason this card needed new SDK vocabulary. "With one or more blue mana
 * symbols in its mana cost" is a read of the *printed cost's pips*, which is not the same question
 * as "is the spell blue": colour is a characteristic layer 5, colour indicators and devoid can
 * change, while `{U}` pips on the card cannot. Two new primitives, sharing one counting rule
 * (`ManaCost.coloredSymbolCount`) so the filter and the token count can never disagree:
 *  - `CardPredicate.ColoredManaSymbolsAtLeast(listOf(BLUE))` — the trigger's "one or more" gate,
 *    reached here through `GameObjectFilter.coloredManaSymbolsAtLeast`.
 *  - `EntityNumericProperty.ColoredManaSymbolCount(listOf(BLUE))` read off
 *    `EntityReference.Triggering` — the "that many" token count.
 *
 * Both follow CR 107.4e/f: a hybrid symbol *is* all of its component colours, so `{U/R}` and
 * `{2/U}` each count as one blue symbol, and a Phyrexian `{U/P}` is blue. `{X}`, generic and `{C}`
 * are no colour at all and count nothing, whatever value was chosen for X (CR 107.4b). What is
 * read is the *printed* cost — the mana symbols printed on the card (CR 202.1/202.1a) — while
 * alternative costs, additional costs and cost reductions only build the spell's *total* cost
 * (CR 601.2f), so none of them move the pip count.
 *
 * The trigger fires on *cast* (CR 601.2i), so the count is taken from a spell that was on the
 * stack; countering that spell in response does not undo the tokens. Pinned by
 * `NamorTheSubMarinerScenarioTest`'s "a counterspell in response does not undo the tokens".
 */
val NamorTheSubMariner = card("Namor the Sub-Mariner") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Mutant Merfolk Villain"
    oracleText = "Flying\n" +
        "Namor's power is equal to the number of Merfolk you control.\n" +
        "Whenever you cast a noncreature spell with one or more blue mana symbols in its mana " +
        "cost, create that many 1/1 blue Merfolk creature tokens."
    dynamicPower(
        DynamicAmounts.battlefield(Player.You, GameObjectFilter.Any.withSubtype(Subtype.MERFOLK)).count()
    )
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Noncreature.coloredManaSymbolsAtLeast(Color.BLUE)
        )
        effect = Effects.CreateToken(
            count = DynamicAmounts.coloredManaSymbolsOf(EntityReference.Triggering, Color.BLUE),
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk"),
            imageUri = "https://cards.scryfall.io/normal/front/7/f/7f7a4f95-f12b-4c28-a6f7-f3c64364abba.jpg?1783902797",
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "69"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7aaefcf9-fbe1-4767-92a5-09825761d116.jpg?1783902956"
    }
}
