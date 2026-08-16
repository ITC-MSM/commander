package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Massacre Girl, Known Killer — Murders at Karlov Manor #94
 * {2}{B}{B} · Legendary Creature — Human Assassin · 4/4 · Mythic
 *
 * Menace
 * Creatures you control have wither.
 * Whenever a creature an opponent controls dies, if its toughness was less than 1, draw a card.
 *
 * The two non-menace abilities are one machine. Wither turns your creatures' damage into -1/-1
 * counters (CR 702.90a), so a creature that "dies to combat damage" against your board doesn't die
 * with its toughness intact — it dies with its toughness *shrunk to zero*, as a state-based action
 * (CR 704.5f). The draw trigger reads exactly that condition, so it fires on the same kills the
 * first ability caused, and stays silent on kills it didn't: a creature that took plain lethal
 * damage from a burn spell dies at toughness 3, not 0, and draws nothing.
 *
 * **The toughness is last-known information.** By the time the trigger is evaluated the creature is
 * already in the graveyard, where it has no toughness at all, so the check has to run against the
 * value it had as it last existed on the battlefield (2024-02-02 ruling). That is why the condition
 * lives in the *trigger filter* rather than as an `interveningIf`: `TriggerMatcher` evaluates a
 * `ToughnessAtMost` predicate against the `lastKnownToughness` carried on the zone-change event,
 * falling back to projected state only for entities that are still around. An intervening-if would
 * be evaluated twice (on trigger and again on resolution) against a value that can no longer be
 * read — and since last-known information is frozen, the second check could only ever be worse.
 *
 * "Toughness was less than 1" is `toughnessAtMost(0)`, not `toughnessAtMost(1)` — the printed text
 * is a strict inequality, and it includes negative toughness, which a pile of -1/-1 counters
 * reaches routinely.
 *
 * Scope notes: the wither grant covers **all** creatures you control, Massacre Girl herself
 * included, and the draw trigger is scoped to creatures an *opponent* controls, so your own losses
 * are never a payoff.
 */
val MassacreGirlKnownKiller = card("Massacre Girl, Known Killer") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Assassin"
    power = 4
    toughness = 4
    oracleText = "Menace\n" +
        "Creatures you control have wither. (They deal damage to creatures in the form of -1/-1 " +
        "counters.)\n" +
        "Whenever a creature an opponent controls dies, if its toughness was less than 1, draw a " +
        "card."

    keywords(Keyword.MENACE)

    staticAbility {
        ability = GrantKeyword(
            Keyword.WITHER,
            GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls().toughnessAtMost(0),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
        description = "Whenever a creature an opponent controls dies, if its toughness was less " +
            "than 1, draw a card."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "94"
        artist = "Billy Christian"
        flavorText = "\"'Did I do it?' You'll have to be more specific.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb1c8800-9d33-485c-b776-042003b9ea92.jpg?1783912894"

        ruling(
            "2024-02-02",
            "Use the toughness of the creature as it last existed on the battlefield to determine " +
                "whether or not Massacre Girl's ability triggers."
        )
        ruling(
            "2024-02-02",
            "Wither applies to any damage dealt to creatures by creatures you control. This " +
                "includes combat damage as well as anything that causes creatures you control to " +
                "deal noncombat damage, such as Incinerator of the Guilty's reflexive triggered " +
                "ability or the effect of Hard-Hitting Question."
        )
    }
}
