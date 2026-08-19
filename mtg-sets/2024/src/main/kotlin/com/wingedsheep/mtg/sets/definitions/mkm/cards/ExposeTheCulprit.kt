package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.TurnFaceUpEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Expose the Culprit — Murders at Karlov Manor #124
 * {1}{R} · Instant · Uncommon
 *
 * Choose one or both —
 * • Turn target face-down creature face up.
 * • Exile any number of face-up creatures you control with disguise in a face-down pile, shuffle
 *   that pile, then cloak them.
 *
 * **"Choose one or both" is the count, not a third mode** — `chooseCount = 2` with
 * `minChooseCount = 1` (CR 700.2), the same shape as Winterflame. Spelling "both" as an extra mode
 * would misreport `SpellCastEvent.chosenModesCount` and give a mode-changing copy effect three
 * modes to pick from instead of two.
 *
 * **Mode 1** is Break Open without the "an opponent controls" clause: any face-down creature is a
 * legal target, including your own. Turning it face up is *not* the special action — no cost is
 * paid and no reveal-a-creature-card requirement applies (CR 701.58c is about the special action,
 * not about effects that turn a permanent face up). The printed ruling for a face-down *instant or
 * sorcery* card is that it is revealed and stays face down, with no turned-face-up trigger;
 * `TurnFaceUpExecutor` already owns that branch, which is why this mode is one effect and not a
 * conditional.
 *
 * **Mode 2 is not a targeted mode.** "Exile any number of face-up creatures you control" prints no
 * "target", so it is a resolution-time choice — a gather → choose → exile → return pipeline, not a
 * `TargetPermanent(unlimited = true)`. That distinction is load-bearing: shroud, protection and
 * "can't be the target of spells" on your own creature do not stop this, and a creature that
 * becomes an illegal target between cast and resolution is still eligible.
 *
 * The `withDisguise()` filter is the **printed** disguise ability (CR 702.168), read off the card
 * in any zone — deliberately not `withMorph()`, which asks the "can this be turned face up"
 * question and would also answer true for a cloaked or manifested permanent. Combined with
 * `faceUp()` it is exactly the card's wording: a disguise creature you already turned face up (or
 * simply hard-cast) can be hidden away again, which is the card's whole trick.
 *
 * **The shuffle is informational.** In paper the pile is shuffled so opponents can't track which
 * cloaked permanent is which (the printed ruling adds "after you cloak the creatures, you may look
 * at them" — the controller keeps the information). This engine hides a face-down permanent's card
 * from everyone but its controller regardless of order, so the shuffle changes no game state; it is
 * expressed as `CardOrder.Random` on the return move so the intent survives in the script rather
 * than being silently dropped.
 *
 * Cloaking is `FaceDownMode.CLOAK` on the move that puts the cards onto the battlefield (CR 701.58)
 * — the 2/2 body and the ward {2} are data on the mode, not abilities this card grants. The cards
 * genuinely pass through exile, so each returns as a **new object**: Auras fall off, counters are
 * gone, and summoning sickness applies afresh.
 */
val ExposeTheCulprit = card("Expose the Culprit") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Turn target face-down creature face up.\n" +
        "• Exile any number of face-up creatures you control with disguise in a face-down pile, " +
        "shuffle that pile, then cloak them. (To cloak a card, put it onto the battlefield face " +
        "down as a 2/2 creature with ward {2}. Turn it face up any time for its mana cost if it's " +
        "a creature card.)"

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Turn target face-down creature face up") {
                val faceDownCreature = target(
                    "face-down creature",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature.faceDown()))
                )
                effect = TurnFaceUpEffect(faceDownCreature)
            }
            mode("Exile any number of face-up creatures you control with disguise, then cloak them") {
                effect = Effects.Pipeline {
                    val candidates = gather(
                        GameObjectFilter.Creature.youControl().faceUp().withDisguise()
                    )
                    // Selecting permanents already in play — the battlefield UI, not an overlay,
                    // so the player can see counters, Auras and which duplicate is which.
                    val chosen = chooseAnyNumber(
                        from = candidates,
                        prompt = "Exile any number of face-up creatures you control with disguise",
                        useTargetingUI = true
                    )
                    val piled = moveTracked(chosen, CardDestination.ToZone(Zone.EXILE))
                    move(
                        piled,
                        CardDestination.ToZone(Zone.BATTLEFIELD),
                        order = CardOrder.Random,
                        faceDown = FaceDownMode.CLOAK
                    )
                }
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Ryan Valle"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31aadd3d-5ce1-44ba-ac6d-b192a9ea491b.jpg?1783912881"

        ruling(
            "2024-02-02",
            "The pile is shuffled to disguise from your opponents which cloaked creature is which. " +
                "After you cloak the creatures, you may look at them."
        )
        ruling(
            "2024-02-02",
            "To cloak a card, put it onto the battlefield face down. It becomes a 2/2 face-down " +
                "creature card with ward {2} and no name, mana cost, or creature types. It's " +
                "colorless and has a mana value of 0. Other effects that apply to the permanent " +
                "can still grant it any characteristics it doesn't have or change the " +
                "characteristics it does have."
        )
        ruling(
            "2024-02-02",
            "If a cloaked creature would have disguise (or morph) if it were face up, you may also " +
                "turn it face up by paying its disguise (or morph) cost."
        )
        ruling(
            "2024-02-02",
            "Because the permanent is on the battlefield both before and after it's turned face " +
                "up, turning a permanent face up doesn't cause any enters-the-battlefield " +
                "abilities to trigger."
        )
        ruling(
            "2024-02-02",
            "If something tries to turn a face-down instant or sorcery card on the battlefield " +
                "face up, reveal that card to show all players it's an instant or sorcery card. " +
                "The permanent remains on the battlefield face down. Abilities that trigger when a " +
                "permanent turns face up won't trigger, because even though you revealed the card, " +
                "it never turned face up."
        )
    }
}
