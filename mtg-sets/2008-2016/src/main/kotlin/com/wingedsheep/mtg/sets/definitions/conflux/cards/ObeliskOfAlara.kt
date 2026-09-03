package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Obelisk of Alara
 * {6}
 * Artifact
 * {1}{W}, {T}: You gain 5 life.
 * {1}{U}, {T}: Draw a card, then discard a card.
 * {1}{B}, {T}: Target creature gets -2/-2 until end of turn.
 * {1}{R}, {T}: This artifact deals 3 damage to target player or planeswalker.
 * {1}{G}, {T}: Target creature gets +4/+4 until end of turn.
 *
 * Five independent activated abilities, one per colour, each a mana-plus-tap [Costs.Composite].
 * They are five abilities rather than one modal effect because each has its own cost — a mode is
 * chosen after a single cost is paid, and nothing here shares one; the tap is separately paid and
 * so only one can be activated per untap step anyway.
 *
 * "Draw a card, then discard a card" is the corpus's standing composition: [Effects.DrawCards]
 * followed by the [Patterns.Hand.discardCards] pipeline (gather hand → choose one → move to
 * graveyard as a discard), both defaulting to the ability's controller.
 *
 * The pump and the shrink are the same [Effects.ModifyStats] with opposite signs; its default
 * duration is already `Duration.EndOfTurn`, so the printed "until end of turn" needs no argument.
 */
val ObeliskOfAlara = card("Obelisk of Alara") {
    manaCost = "{6}"
    colorIdentity = "BGRUW"
    typeLine = "Artifact"
    oracleText = "{1}{W}, {T}: You gain 5 life.\n" +
        "{1}{U}, {T}: Draw a card, then discard a card.\n" +
        "{1}{B}, {T}: Target creature gets -2/-2 until end of turn.\n" +
        "{1}{R}, {T}: This artifact deals 3 damage to target player or planeswalker.\n" +
        "{1}{G}, {T}: Target creature gets +4/+4 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        effect = Effects.GainLife(5)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        effect = Effects.Composite(
            Effects.DrawCards(1),
            Patterns.Hand.discardCards(1)
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap)
        val victim = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-2, -2, victim)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap)
        val victim = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(3, victim)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.Tap)
        val beneficiary = target("target", Targets.Creature)
        effect = Effects.ModifyStats(4, 4, beneficiary)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Jeremy Jarvis"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cc12ebe-54d8-4b91-8c68-3cde5690e26a.jpg"
    }
}
