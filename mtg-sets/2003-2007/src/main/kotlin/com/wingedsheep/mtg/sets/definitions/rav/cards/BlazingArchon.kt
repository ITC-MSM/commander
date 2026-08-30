package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeAttackedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Blazing Archon
 * {6}{W}{W}{W}
 * Creature — Archon
 * 5/6
 *
 * Flying
 * Creatures can't attack you.
 *
 * The unqualified sentence is the base case of [CantBeAttackedBy] — the filter carries the whole
 * restriction, so "creatures" with no qualifier is the bare [GameObjectFilter.Creature]. The
 * engine consults the *defending* player's battlefield for this ability, which is why the Archon
 * protects its controller rather than itself, and why it keeps working while it is tapped.
 *
 * It is deliberately an attack restriction and nothing more: per the ruling below a creature that
 * can't attack you may still attack a planeswalker you control, which falls out for free because
 * CR 508.1c is checked against the *player* being attacked.
 */
val BlazingArchon = card("Blazing Archon") {
    manaCost = "{6}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Archon"
    oracleText = "Flying\n" +
        "Creatures can't attack you."
    power = 5
    toughness = 6

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantBeAttackedBy(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"Through the haze of battle I saw the glint of sun on golden mane, the sheen " +
            "of glory clad in mail, and I dropped my sword and wept at the idiocy of war.\"\n" +
            "—Dravin, Gruul deserter"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd0bc05b-ec48-41fd-a97a-ffb0d5a2dee0.jpg?1783943707"
        ruling(
            "2014-02-01",
            "Unless some effect explicitly says otherwise, a creature that can't attack you can " +
                "still attack a planeswalker you control."
        )
    }
}
