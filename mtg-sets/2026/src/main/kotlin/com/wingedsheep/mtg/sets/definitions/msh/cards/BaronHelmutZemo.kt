package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect

/**
 * Baron Helmut Zemo
 * {B}{B}{B}
 * Legendary Creature — Human Noble Villain
 * 3/3
 *
 * Whenever you cast a black spell from your hand, Baron Helmut Zemo connives.
 * Boast — Exile any number of black cards from your graveyard with fifteen or more black mana
 * symbols among their mana costs: Copy those exiled cards. You may cast up to three of the copies
 * without paying their mana costs.
 *
 * **The trigger** is the ordinary cast trigger with two riders that already exist: a colour filter
 * on the spell (`withColor(BLACK)` — the spell's *colour*, which is what the printed text says) and
 * `SpellCastPredicate.CastFromZone(HAND)`, which is what keeps a flashbacked or graveyard-cast black
 * spell from firing it. [Effects.Connive] on the default `Self` target is "Baron Helmut Zemo
 * connives", the same shape Madame Masque uses in this set.
 *
 * **Boast** (CR 702.142a) is `isBoast = true`: the DSL renders the "Boast — " prefix and installs the
 * keyword's two rules clauses as ordinary activation restrictions — `OncePerTurn` and
 * `SourceAttackedThisTurn`. Nothing about the window is bespoke; "attacked this turn" is tracked for
 * the whole turn, so the boast is still available in the postcombat main phase and the end step.
 *
 * **The cost** is the one genuinely new shape: the constraint is a floor on a *summed* quantity
 * across the chosen cards, not on how many are chosen.
 * [Costs.ExileFromGraveyardForColoredSymbols] is the sum-gated graveyard exile
 * (`CostAtom.ExileFromGraveyardForTotal`) measured by black mana symbols. Consequences worth
 * stating, because they are what the printed wording actually means:
 *  - **any number** — five cards or two, whichever reaches fifteen pips; a black card with no black
 *    pip in its cost (a `{2}` artifact made black, say) is a legal pick worth nothing;
 *  - **overpaying is legal** — nothing caps the selection at exactly fifteen;
 *  - hybrid and Phyrexian pips count for their colour (CR 107.4e/f), `{X}` and generic count for
 *    none — the same counting rule Namor the Sub-Mariner's per-card filter uses, so the two can
 *    never disagree;
 *  - the cost is **unpayable, and so the ability is not offered at all**, when your black graveyard
 *    cards don't add up to fifteen. Fifteen is a real deck-building ask: it is five `{B}{B}{B}`
 *    cards, or three of this very card.
 *
 * **The effect** is the "copy a set of cards, then cast some of them" composition The Tale of Tamiyo
 * chapter IV already uses, with two pieces this card needed and now shares:
 * [CardSource.ExiledAsCost] names the cards the *cost* just exiled (they are in exile by resolution,
 * and each activation sees only its own payment — unlike a linked-exile pile, which would hand a
 * second boast the first one's cards), and
 * [Effects.CastUpToNFromCollectionWithoutPayingCost] bounds the free casts at three. The copies are
 * created in exile (CR 707.12); any that go uncast cease to exist as a state-based action
 * (CR 707.10a), while the exiled *originals* stay exiled.
 */
val BaronHelmutZemo = card("Baron Helmut Zemo") {
    manaCost = "{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Noble Villain"
    power = 3
    toughness = 3
    oracleText = "Whenever you cast a black spell from your hand, Baron Helmut Zemo connives. " +
        "(Draw a card, then discard a card. If you discarded a nonland card, put a +1/+1 counter " +
        "on this creature.)\n" +
        "Boast — Exile any number of black cards from your graveyard with fifteen or more black " +
        "mana symbols among their mana costs: Copy those exiled cards. You may cast up to three " +
        "of the copies without paying their mana costs. (Activate only if this creature attacked " +
        "this turn and only once each turn.)"

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withColor(Color.BLACK),
            requires = setOf(SpellCastPredicate.CastFromZone(Zone.HAND)),
        )
        effect = Effects.Connive()
        description = "Whenever you cast a black spell from your hand, Baron Helmut Zemo connives."
    }

    activatedAbility {
        isBoast = true
        cost = Costs.ExileFromGraveyardForColoredSymbols(15, Color.BLACK)
        effect = Effects.Composite(
            GatherCardsEffect(source = CardSource.ExiledAsCost, storeAs = "zemoExiled"),
            Effects.CopyCollectionIntoCollection(from = "zemoExiled", storeAs = "zemoCopies"),
            Effects.CastUpToNFromCollectionWithoutPayingCost(from = "zemoCopies", maxCasts = 3),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "87"
        artist = "Wero Gallo"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/" +
            "c2aadc25-7755-4bc8-a8af-b01d27eec364.jpg?1783902947"
    }
}
