package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Esper Origins // Summon: Esper Maduin — Final Fantasy #185
 * {1}{G} · Sorcery
 * // Enchantment Creature — Saga Elemental 4/4
 *
 * Front — Esper Origins:
 *   Surveil 2. You gain 2 life. If this spell was cast from a graveyard, exile it, then put it
 *   onto the battlefield transformed under its owner's control with a finality counter on it.
 *   Flashback {3}{G}
 *
 * Back — Summon: Esper Maduin:
 *   (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 *   I — Reveal the top card of your library. If it's a permanent card, put it into your hand.
 *   II — Add {G}{G}.
 *   III — Other creatures you control get +2/+2 and gain trample until end of turn.
 *
 * Cast normally, the front is an ordinary sorcery that surveils and gains life, then goes to the
 * graveyard. Flashed back (or otherwise cast from a graveyard), the same resolution instead exiles
 * the card and returns it to the battlefield transformed as the back-face Saga with a finality
 * counter — modelled by [com.wingedsheep.sdk.model.CardScript.returnTransformedFromGraveyardOnResolve]
 * (the `returnTransformedFromGraveyard` spell-builder marker). The destination is derived from the
 * spell's cast-from zone at resolution time, exactly like flashback's own graveyard-cast exile, so
 * it survives the mid-resolution pause of Surveil and takes precedence over the flashback exile.
 * The finality counter (CR 122.1h — "if it would die, exile it instead") keeps the loop from
 * refilling the graveyard for another flashback.
 */
private val SummonEsperMaduin = card("Summon: Esper Maduin") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Saga Elemental"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Reveal the top card of your library. If it's a permanent card, put it into your hand.\n" +
        "II — Add {G}{G}.\n" +
        "III — Other creatures you control get +2/+2 and gain trample until end of turn."
    power = 4
    toughness = 4

    // I — Reveal the top card; if it's a permanent card, put it into your hand. A non-permanent card
    // is revealed but stays on top of the library in place (CardOrder.Preserve, top placement).
    sagaChapter(1) {
        effect = Patterns.Library.revealTopPutAllMatchingToHand(
            count = DynamicAmount.Fixed(1),
            filter = GameObjectFilter.Permanent,
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top),
            restOrder = CardOrder.Preserve,
        )
    }

    // II — Add {G}{G}. (A triggered ability, not a mana ability — it uses the stack.)
    sagaChapter(2) {
        effect = Effects.AddMana(Color.GREEN, 2)
    }

    // III — Other creatures you control get +2/+2 and gain trample until end of turn.
    sagaChapter(3) {
        val otherCreaturesYouControl = GroupFilter(
            GameObjectFilter.Creature.youControl(),
            excludeSelf = true,
        )
        effect = Effects.Composite(
            Patterns.Group.modifyStatsForAll(
                power = 2,
                toughness = 2,
                filter = otherCreaturesYouControl,
            ),
            Patterns.Group.grantKeywordToAll(
                keyword = Keyword.TRAMPLE,
                filter = otherCreaturesYouControl,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Danciao"
        flavorText = "\"I've chosen a name for her . . . Terra.\""
        imageUri = "https://cards.scryfall.io/normal/back/0/f/0f503360-216a-4629-89b2-d32072850aef.jpg?1782686461"
    }
}

private val EsperOriginsFront = card("Esper Origins") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Surveil 2. You gain 2 life. If this spell was cast from a graveyard, exile it, " +
        "then put it onto the battlefield transformed under its owner's control with a finality " +
        "counter on it. (If a creature with a finality counter on it would die, exile it instead.)\n" +
        "Flashback {3}{G} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.Composite(
            Effects.Surveil(2),
            Effects.GainLife(2),
        )
        // If cast from a graveyard, this resolution puts the card onto the battlefield transformed
        // (as Summon: Esper Maduin) with a finality counter, instead of going to the graveyard.
        returnTransformedFromGraveyard(CounterType.FINALITY)
    }

    keywordAbility(KeywordAbility.flashback("{3}{G}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Solan"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f503360-216a-4629-89b2-d32072850aef.jpg?1782686461"
    }
}

/**
 * Esper Origins is a transforming double-faced card: a sorcery front joined to the Saga-creature
 * back [SummonEsperMaduin]. The front is a non-permanent face, so the card is assembled with a raw
 * `copy(backFace = …)` rather than the permanent-front DFC factories.
 */
val EsperOrigins: CardDefinition = EsperOriginsFront.copy(backFace = SummonEsperMaduin)
