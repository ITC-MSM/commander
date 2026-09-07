package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ringskipper
 * {1}{U}
 * Creature — Faerie Wizard
 * 1/1
 *
 * Flying
 * When this creature dies, clash with an opponent. If you win, return this card to its owner's hand.
 *
 * The clash is unconditional and the *return* is the win rider, so the whole ability is
 * [Patterns.Mechanic.clash] with the return on the `ifYouWin` leg — a lost clash still resolved the
 * clash itself, which matters for "whenever you clash" watchers on either side of the table.
 *
 * By the time the dies trigger resolves the source is already in its owner's graveyard, so
 * [EffectTarget.Self] picks it up there (Angelic Destiny's shape). The move is
 * [Effects.ReturnToHandFromGraveyard] rather than a plain return because the printed ruling calls
 * the guard out explicitly — "if Ringskipper is removed from the graveyard before the ability
 * resolves, you still clash, but nothing will happen if you win" — and that `fromZone = GRAVEYARD`
 * guard is the only thing that re-examines the card at resolution, since the clause names no target.
 */
val Ringskipper = card("Ringskipper") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature dies, clash with an opponent. If you win, return this card to its " +
        "owner's hand. (Each clashing player reveals the top card of their library, then puts that " +
        "card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Mechanic.clash(
            Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        )
        description = "Clash with an opponent. If you win, return this card to its owner's hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Heather Hudson"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72d3ae1f-5442-4725-a16a-502f13359a85.jpg?1783942899"
        ruling(
            "2007-10-01",
            "If Ringskipper is removed from the graveyard before the ability resolves, you still " +
                "clash, but nothing will happen if you win."
        )
    }
}
