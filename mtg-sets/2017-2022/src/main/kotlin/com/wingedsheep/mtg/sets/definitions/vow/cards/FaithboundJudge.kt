package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Faithbound Judge // Sinner's Judgment — Innistrad: Crimson Vow #12
 * {1}{W}{W} · Creature — Spirit Soldier 4/4 // Enchantment — Aura Curse
 *
 * Front — Faithbound Judge
 *   Defender, flying, vigilance
 *   At the beginning of your upkeep, if this creature has two or fewer judgment counters on it,
 *   put a judgment counter on it.
 *   As long as this creature has three or more judgment counters on it, it can attack as though it
 *   didn't have defender.
 *   Disturb {5}{W}{W}
 *
 * Back — Sinner's Judgment
 *   Enchant player
 *   At the beginning of your upkeep, put a judgment counter on this Aura. Then if there are three
 *   or more judgment counters on it, enchanted player loses the game.
 *   If Sinner's Judgment would be put into a graveyard from anywhere, exile it instead.
 *
 * Modeling notes:
 *
 *  - **The two faces do not share a tally.** They are different objects (CR 400.7): a Judge that
 *    died with three judgment counters comes back from the graveyard as a fresh Sinner's Judgment
 *    with none, and has to count to three again. Nothing in the model carries the count across —
 *    each face reads counters off *itself* — so that falls out for free.
 *  - **Front: a true intervening "if" (CR 603.4), back: a resolution-time "then if".** The
 *    difference is printed. The Judge's clause gates the trigger *and* is rechecked on resolution,
 *    so it rides `interveningIf`; a fourth counter can therefore never land. The Aura's clause is
 *    the word "Then", which is checked only while the ability resolves and *after* the counter has
 *    been added — so it is a [ConditionalEffect] sequenced behind the add, and the third counter
 *    kills the enchanted player on the very upkeep it lands.
 *  - **"three or more"/"two or fewer" are counter-count conditions on the source**, not board
 *    conditions: [Conditions.SourceCounterCountAtLeast] and its downward twin
 *    [Conditions.SourceCounterCountAtMost].
 *  - **The defender bypass is a *conditional static*, not a one-shot grant.** Per the printed
 *    ruling, removing judgment counters after the Judge has attacked won't remove it from combat —
 *    which is exactly what a static ability checked at attack declaration does.
 *  - **The Aura enchants a player**, so `auraTarget = Targets.Player` (Curse of Hospitality's slot
 *    in this same set) and the loser is [Player.EnchantedPlayer], not the controller — the Curse
 *    kills whoever it is attached to, and its controller is the one asking for that.
 *  - **The exile-instead clause is [RedirectZoneChange] with `selfOnly = true`** so it functions
 *    in every zone (CR 614.12) — the same shape Spectral Binding uses. Without it the Curse would
 *    return to the graveyard and be disturbable again, which is the whole point of the clause.
 */
private val FaithboundJudgeFront = card("Faithbound Judge") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    power = 4
    toughness = 4
    oracleText = "Defender, flying, vigilance\n" +
        "At the beginning of your upkeep, if this creature has two or fewer judgment counters on " +
        "it, put a judgment counter on it.\n" +
        "As long as this creature has three or more judgment counters on it, it can attack as " +
        "though it didn't have defender.\n" +
        "Disturb {5}{W}{W} (You may cast this card from your graveyard transformed for its " +
        "disturb cost.)"

    keywords(Keyword.DEFENDER, Keyword.FLYING, Keyword.VIGILANCE)

    // At the beginning of your upkeep, if this creature has two or fewer judgment counters on it,
    // put a judgment counter on it.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.SourceCounterCountAtMost(Counters.JUDGMENT, 2)
        effect = Effects.AddCounters(Counters.JUDGMENT, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, if this creature has two or fewer " +
            "judgment counters on it, put a judgment counter on it."
    }

    // As long as this creature has three or more judgment counters on it, it can attack as though
    // it didn't have defender.
    staticAbility {
        ability = CanAttackDespiteDefender(
            condition = Conditions.SourceCounterCountAtLeast(Counters.JUDGMENT, 3)
        )
    }

    disturb("{5}{W}{W}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "12"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db791fb6-b0ff-4ded-bd3d-9447cf398312.jpg?1783951787"

        ruling(
            "2021-11-19",
            "Removing judgment counters from Faithbound Judge after it has attacked won't remove " +
                "it from combat."
        )
    }
}

private val SinnersJudgment = card("Sinner's Judgment") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W" // Transformed back face, no mana cost (CR 204).
    typeLine = "Enchantment — Aura Curse"
    oracleText = "Enchant player\n" +
        "At the beginning of your upkeep, put a judgment counter on this Aura. Then if there are " +
        "three or more judgment counters on it, enchanted player loses the game.\n" +
        "If Sinner's Judgment would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Player

    // At the beginning of your upkeep, put a judgment counter on this Aura. Then if there are
    // three or more judgment counters on it, enchanted player loses the game.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Effects.AddCounters(Counters.JUDGMENT, 1, EffectTarget.Self),
            ConditionalEffect(
                condition = Conditions.SourceCounterCountAtLeast(Counters.JUDGMENT, 3),
                effect = Effects.LoseGame(
                    target = EffectTarget.PlayerRef(Player.EnchantedPlayer),
                    message = "Sinner's Judgment"
                ),
            ),
        )
        description = "At the beginning of your upkeep, put a judgment counter on this Aura. Then " +
            "if there are three or more judgment counters on it, enchanted player loses the game."
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "12"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/back/d/b/db791fb6-b0ff-4ded-bd3d-9447cf398312.jpg?1783951787"
    }
}

val FaithboundJudge: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = FaithboundJudgeFront,
    backFace = SinnersJudgment,
)
