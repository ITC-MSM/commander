package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Pompous Gadabout — Murders at Karlov Manor #171
 * {2}{G} · Creature — Human Citizen · 4/2
 *
 * During your turn, this creature has hexproof.
 * This creature can't be blocked by creatures that don't have a name.
 *
 * Hexproof is a [GrantKeyword] gated by [Conditions.IsYourTurn] — the `condition` field on the
 * `staticAbility { }` block auto-wraps it in a `ConditionalStaticAbility`, so the keyword is simply
 * absent from projected state on opponents' turns rather than being granted and then stripped.
 *
 * "Creatures that don't have a name" is the face-down set. CR 708.2 gives a face-down permanent no
 * name, and this card's own ruling states it outright: *"Face-down creatures don't have names unless
 * an effect says otherwise."* In an MKM context that means disguised and cloaked creatures, which is
 * the whole point of the design. So the blocker filter is `Creature.faceDown()`, reading the
 * projected face-down state rather than the underlying card — a face-down creature turned face up
 * mid-combat has a name again and the restriction stops applying to it, exactly as the rules want.
 *
 * The restriction is checked when blockers are declared, so per the second ruling, turning an
 * already-declared blocker face down doesn't retroactively remove it from combat.
 */
val PompousGadabout = card("Pompous Gadabout") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Citizen"
    power = 4
    toughness = 2
    oracleText = "During your turn, this creature has hexproof.\n" +
        "This creature can't be blocked by creatures that don't have a name."

    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(Keyword.HEXPROOF, Filters.Self)
    }

    staticAbility {
        ability = CantBeBlockedBy(blockerFilter = GameObjectFilter.Creature.faceDown())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        artist = "Scott Murphy"
        flavorText = "\"Stand aside, nobodies.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d803b93-c1df-4a02-9dbb-d347c841d4d7.jpg?1783912863"
        ruling("2024-02-02", "Face-down creatures don't have names unless an effect says otherwise.")
        ruling(
            "2024-02-02",
            "Once Pompous Gadabout has been blocked by a creature, turning that creature face down " +
                "or otherwise causing it to lose its name won't cause it to stop blocking Pompous Gadabout."
        )
    }
}
