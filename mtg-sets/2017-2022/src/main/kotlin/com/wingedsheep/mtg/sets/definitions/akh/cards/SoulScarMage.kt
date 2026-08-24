package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DamageCounterRecipient
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ReplaceDamageWithCounters
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Soul-Scar Mage — Amonkhet #148 (canonical printing)
 * {R} · Creature — Human Wizard · 1/2
 *
 * Prowess
 * If a source you control would deal noncombat damage to a creature an opponent controls, put
 * that many -1/-1 counters on that creature instead.
 *
 * A one-mana prowess body that turns every burn spell, ping and fight into permanent shrinkage.
 * The second ability is a **replacement effect** (CR 614.1a — "instead"), not a prevention effect:
 * the damage is never dealt, so nothing keying on damage being dealt sees it, and — per the card's
 * own ruling — it still applies to damage that can't be prevented.
 *
 * Every part of the clause is carried by the event pattern rather than by bespoke code:
 * `source = YouControl` for "a source you control" (a permanent, a spell on the stack, or an
 * ability alike), `damageType = NonCombat` so combat damage is untouched, and
 * `recipient = CreatureOpponentControls` for the creatures it applies to. The counters land on the
 * damaged creature rather than on the Mage, which is [DamageCounterRecipient.DamagedPermanent].
 *
 * Fight damage is noncombat damage, so a fight an opponent's creature loses leaves -1/-1 counters
 * behind (2017-04-18 ruling) — that falls out of the pattern without a special case.
 *
 * **Known deviation — CR 616.1 ordering is not modelled.** When this replacement competes with
 * another replacement or prevention effect, CR 616.1 gives the choice of which to apply first to
 * "the affected object's controller" — the opponent, here — and the card's own 2017-04-18 ruling
 * spells out both directions they would use it in (converting the damage to counters *before*
 * Insult could double it, so Insult no longer applies; or taking Djeru's Resolve's prevention over
 * the counters). The engine has no 616.1 prompt at all: it amplifies before it replaces, and it
 * places counters ahead of a shield counter — in both cases the order the affected player would
 * not have chosen. That is engine-wide behaviour rather than anything specific to this card, and
 * the `ruling(...)` block below ships the ordering ruling verbatim, so this note is here to keep
 * the two from reading as a contradiction.
 */
val SoulScarMage = card("Soul-Scar Mage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until " +
        "end of turn.)\n" +
        "If a source you control would deal noncombat damage to a creature an opponent controls, " +
        "put that many -1/-1 counters on that creature instead."

    prowess()

    replacementEffect(
        ReplaceDamageWithCounters(
            counterType = Counters.MINUS_ONE_MINUS_ONE,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.CreatureOpponentControls,
                source = SourceFilter.YouControl,
                damageType = DamageType.NonCombat
            ),
            counterRecipient = DamageCounterRecipient.DamagedPermanent
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Steve Argyle"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8003786-6e2a-4e2d-a915-f23293c7273a.jpg?1783936483"
        ruling(
            "2017-04-18",
            "If an effect causes a creature you control to fight a creature an opponent controls, " +
                "the damage is dealt simultaneously. For instance, if your 2/2 creature fights an " +
                "opponent's 4/4 creature, your creature will be dealt 4 damage and your opponent's " +
                "creature will have two -1/-1 counters put on it."
        )
        ruling(
            "2017-04-18",
            "Soul-Scar Mage's effect isn't a prevention effect. Damage that can't be prevented " +
                "will be replaced with -1/-1 counters."
        )
        ruling(
            "2017-04-18",
            "If multiple prevention and/or replacement effects are trying to apply to the same " +
                "damage, the controller of the creature that would be dealt damage chooses the " +
                "order in which to apply them."
        )
    }
}
