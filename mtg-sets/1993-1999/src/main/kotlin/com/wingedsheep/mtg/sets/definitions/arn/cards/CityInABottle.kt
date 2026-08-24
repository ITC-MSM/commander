package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.PlayersCantPlayLands
import com.wingedsheep.sdk.scripting.references.Player

/**
 * City in a Bottle
 * {2}
 * Artifact
 * Whenever one or more other nontoken permanents with a name originally printed in the Arabian
 * Nights expansion are on the battlefield, their controllers sacrifice them.
 * Players can't cast spells or play lands with a name originally printed in the Arabian Nights
 * expansion.
 *
 * Composition — three existing primitives over one set-membership filter:
 *  - `originallyPrintedInSet("ARN")` ([com.wingedsheep.sdk.scripting.predicates.CardPredicate.OriginallyPrintedInSet])
 *    is the whole card's vocabulary. It reads `CardComponent.originalSetCode`, the *canonical*
 *    set of the definition rather than the printing a player owns, so a reprint of an Arabian
 *    Nights card still matches — the 2014-02-01 ruling's "even if the physical card representing
 *    that permanent is a reprint with a different expansion symbol". Tokens have no original set,
 *    so they never match, which is also the 2004-10-04 ruling.
 *  - The first line is a `stateTriggeredAbility` (CR 603.8), not an enters/leaves trigger: it is
 *    checked each time a player would receive priority, which is exactly what the ruling
 *    describes. `excludeSelf = true` supplies the printed "other" — City in a Bottle's own name
 *    was originally printed in ARN, so without it the card would hold its own condition true
 *    forever and sacrifice itself on sight. The effect is `SacrificeAll(excludeTriggering = true)`,
 *    which sacrifices each matching permanent under its own controller (CR 701.17) — "their
 *    controllers sacrifice them" — and leaves this artifact alone.
 *  - The second line is the pair of lock statics scoped by the same filter:
 *    [PlayersCantCastSpells] (read at cast-legality time, so it covers every casting zone) and
 *    [PlayersCantPlayLands] (playing a land is a special action, never a cast, so it needs its own
 *    line — the same split Worms of the Earth prints). Both are `Player.Each`: the printed text
 *    says "Players", including this card's own controller.
 *
 * Note the two lines are deliberately not redundant. The lock stops new Arabian Nights cards
 * arriving by cast or land drop; the state trigger catches the ones already on the battlefield
 * and the ones that arrive by any other route (a search effect, a reanimation, a copy).
 */
val CityInABottle = card("City in a Bottle") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever one or more other nontoken permanents with a name originally printed " +
        "in the Arabian Nights expansion are on the battlefield, their controllers sacrifice them.\n" +
        "Players can't cast spells or play lands with a name originally printed in the Arabian " +
        "Nights expansion."

    stateTriggeredAbility {
        condition = Conditions.AnyPlayerControls(
            GameObjectFilter.Permanent.nontoken().originallyPrintedInSet("ARN"),
            excludeSelf = true
        )
        effect = Effects.SacrificeAll(
            filter = GameObjectFilter.Permanent.nontoken().originallyPrintedInSet("ARN"),
            excludeTriggering = true
        )
        description = "Whenever one or more other nontoken permanents with a name originally " +
            "printed in the Arabian Nights expansion are on the battlefield, their controllers " +
            "sacrifice them"
    }

    staticAbility {
        ability = PlayersCantCastSpells(
            affected = Player.Each,
            spellFilter = GameObjectFilter.Any.originallyPrintedInSet("ARN")
        )
    }

    staticAbility {
        ability = PlayersCantPlayLands(
            affected = Player.Each,
            landFilter = GameObjectFilter.Any.originallyPrintedInSet("ARN")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "60"
        artist = "Drew Tucker"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/9598b346-a47d-4c4c-9571-156824e86b9c.jpg?1783948377"
        ruling(
            "2014-02-01",
            "Any time a player receives priority to cast spells or activate abilities, check to " +
                "see whether any permanents on the battlefield were originally printed in the " +
                "Arabian Nights expansion (even if the physical card representing that permanent " +
                "is a reprint with a different expansion symbol). If there are any such " +
                "permanents, the ability will trigger and those permanents will be sacrificed."
        )
        ruling("2004-10-04", "Token creatures and counters created by Arabian Nights cards are not removed.")
    }
}
