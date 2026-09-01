package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Twilight Drover — Ravnica: City of Guilds #33
 * {2}{W} · Creature — Spirit · Rare
 *
 * Whenever a creature token leaves the battlefield, put a +1/+1 counter on this creature.
 * {2}{W}, Remove a +1/+1 counter from this creature: Create two 1/1 white Spirit creature tokens
 * with flying.
 *
 * A token engine that turns every token's death into two more: each counter it banks buys a pair of
 * fliers, and those fliers feed it again when they go.
 *
 * **"Leaves the battlefield", not "dies".** The trigger is a bare `from = BATTLEFIELD` zone change
 * with no destination — the first ruling is explicit that where the token went doesn't matter, so
 * exile and bounce count exactly as much as the graveyard. It is [TriggerBinding.ANY]-bound and has
 * no controller predicate: an *opponent's* token leaving feeds the Drover too.
 *
 * **The token filter carries [CardPredicate.IsToken] on purpose** — this fires on creature *tokens*
 * only, so a real creature card leaving does nothing. The Drover's own Spirit tokens match it, which
 * is the loop the third ruling calls out.
 *
 * The counter is the *cost* of the token-making ability, so it is spent on activation and the
 * ability cannot be activated without one; the Drover's own tokens then pay it back as they die.
 *
 * Ravnica predates token *cards* (Scryfall has no `trav` set) and the set self-hosts art only for
 * its Saproling, so this token falls through to the engine-wide generic Spirit art — the same
 * 1/1 white flier Transluminant makes.
 */
val TwilightDrover = card("Twilight Drover") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever a creature token leaves the battlefield, put a +1/+1 counter on this " +
        "creature.\n" +
        "{2}{W}, Remove a +1/+1 counter from this creature: Create two 1/1 white Spirit creature " +
        "tokens with flying."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withCardPredicate(CardPredicate.IsToken),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever a creature token leaves the battlefield, put a +1/+1 counter on " +
            "this creature."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{W}"),
            Costs.RemoveCounterFromSelf(Counters.PLUS_ONE_PLUS_ONE),
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            count = 2,
        )
        description = "{2}{W}, Remove a +1/+1 counter from this creature: Create two 1/1 white " +
            "Spirit creature tokens with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "33"
        artist = "Dave Allsop"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbc33bb6-d71c-45f9-9030-bdef3d40a08a.jpg?1783943693"
        ruling(
            "2005-10-01",
            "The first ability only cares that creature tokens leave the battlefield. It doesn't " +
                "matter where they went."
        )
        ruling(
            "2005-10-01",
            "If a player leaves a multiplayer game, all creature tokens that player owns also " +
                "leave the game. Twilight Drover's ability will trigger once per token."
        )
        ruling(
            "2005-10-01",
            "If Spirit tokens created by Twilight Drover leave the battlefield, they will trigger " +
                "its first ability."
        )
        ruling(
            "2005-10-01",
            "Note that Twilight Drover doesn't have any way to sacrifice tokens or otherwise cause " +
                "them to leave the battlefield."
        )
    }
}
