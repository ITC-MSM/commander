package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Throne of the Grim Captain // The Grim Captain (CR 702.167, The Lost Caverns of Ixalan #266)
 * {2}
 * Legendary Artifact // Legendary Creature — Skeleton Spirit Pirate
 *
 * Front face — Throne of the Grim Captain ({2}, Legendary Artifact)
 *   {T}: Mill two cards.
 *   Craft with a Dinosaur, a Merfolk, a Pirate, and a Vampire {4}
 *
 * Back face — The Grim Captain (Legendary Creature — Skeleton Spirit Pirate, 7/7)
 *   Menace, trample, lifelink, hexproof
 *   Whenever The Grim Captain attacks, each opponent sacrifices a nonland permanent of
 *   their choice. Then you may put an exiled creature card used to craft The Grim Captain
 *   onto the battlefield under your control tapped and attacking.
 *
 * Fully implemented from composable primitives — the two mechanics the printed card needs beyond
 * the existing craft/pipeline vocabulary were added as reusable SDK/engine features:
 *  - **Heterogeneous slot-based Craft** — the front `craft(slots = ...)` overload. This craft names
 *    one material of *each* of four subtypes, so validating a chosen set is a bipartite
 *    perfect-matching problem (a single Merfolk Pirate fills only one slot; four Vampires cannot
 *    cover Dinosaur/Merfolk/Pirate/Vampire). `AbilityCost.Craft.slots` carries the per-slot filters
 *    and `CraftSlotMatching` (engine) does the matching in `canPay`, the legal-action enumerator,
 *    and payment. The union of the slots stays in `filter`, so the flat BF+GY candidate gathering
 *    and the client's material overlay work unchanged.
 *  - **`CardSource.CraftedMaterials`** — the gather-pipeline twin of `DonorCards.CRAFT_MATERIALS`,
 *    reading the source's `CraftedFromExiledComponent` (the cards exiled to craft it) filtered to
 *    those still in exile. It powers the attack trigger's second sentence.
 *
 * Wiring:
 *  - Front tap ability: [Costs.Tap] + [Patterns.Library.mill] (2).
 *  - Back-face keywords: menace, trample, lifelink, hexproof as plain [Keyword]s.
 *  - Back-face attack trigger ([Triggers.Attacks]) is one [Effects.Composite]:
 *      1. `Effects.Sacrifice(NonlandPermanent, EachOpponent)` — the edict; each opponent chooses
 *         their own nonland permanent (Tithing Blade's shape, widened to nonland permanent).
 *      2. gather [CardSource.CraftedMaterials] → [SelectionMode.ChooseUpTo] `1` filtered to
 *         [GameObjectFilter.Creature] ("you may ... an exiled creature card") → move to
 *         [Zone.BATTLEFIELD] with [ZonePlacement.TappedAndAttacking] under your control (Gishath's
 *         "put ... onto the battlefield tapped and attacking" shape).
 *  - Back-face color: the printed back has a black COLOR INDICATOR (empty mana cost), modelled with
 *    `colorIndicator = "B"` (CR 204) so the projected face actually reads as black — not the older
 *    `colorIdentity`-only approximation that left it colourless.
 *
 * Note on client UX for the heterogeneous craft: the legal action ships the *union* of the four
 * slots as one flat material list with min = max = 4, so the overlay lets you pick any four
 * eligible cards. The engine rejects a four-card set that can't fill each distinct slot (e.g. four
 * Vampires) at payment time. Per-slot selection UI is intentionally not added for this one card.
 */

/** One material slot per named subtype (CR 702.167). Each is filled by a distinct material. */
private val CraftSlots: List<GameObjectFilter> = listOf(
    GameObjectFilter().withSubtype(Subtype.DINOSAUR),
    GameObjectFilter().withSubtype(Subtype.MERFOLK),
    GameObjectFilter().withSubtype(Subtype.PIRATE),
    GameObjectFilter().withSubtype(Subtype.VAMPIRE),
)

private val ThroneOfTheGrimCaptainFront = card("Throne of the Grim Captain") {
    manaCost = "{2}"
    colorIdentity = "B"
    typeLine = "Legendary Artifact"
    oracleText = "{T}: Mill two cards.\n" +
        "Craft with a Dinosaur, a Merfolk, a Pirate, and a Vampire {4} ({4}, Exile this artifact, Exile the four from among permanents you control and/or cards in your graveyard: Return this card transformed under its owner's control. Craft only as a sorcery.)"

    // {T}: Mill two cards.
    activatedAbility {
        cost = Costs.Tap
        effect = Patterns.Library.mill(2)
    }

    // Craft with a Dinosaur, a Merfolk, a Pirate, and a Vampire {4}
    craft(
        slots = CraftSlots,
        cost = "{4}",
        materialDescription = "a Dinosaur, a Merfolk, a Pirate, and a Vampire"
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Tiffany Turrill"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c13e8e3d-2a6b-4782-a3c9-71af7336a881.jpg?1782694399"
    }
}

private val TheGrimCaptain = card("The Grim Captain") {
    manaCost = ""
    colorIdentity = "B"
    colorIndicator = "B" // Printed with a black color indicator, no mana cost (CR 204).
    typeLine = "Legendary Creature — Skeleton Spirit Pirate"
    power = 7
    toughness = 7
    oracleText = "Menace, trample, lifelink, hexproof\n" +
        "Whenever The Grim Captain attacks, each opponent sacrifices a nonland permanent of their choice. Then you may put an exiled creature card used to craft The Grim Captain onto the battlefield under your control tapped and attacking."

    keywords(Keyword.MENACE, Keyword.TRAMPLE, Keyword.LIFELINK, Keyword.HEXPROOF)

    // Whenever The Grim Captain attacks, each opponent sacrifices a nonland permanent of their
    // choice. Then you may put an exiled creature card used to craft The Grim Captain onto the
    // battlefield under your control tapped and attacking.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            listOf(
                // "each opponent sacrifices a nonland permanent of their choice"
                Effects.Sacrifice(
                    GameObjectFilter.NonlandPermanent,
                    target = EffectTarget.PlayerRef(Player.EachOpponent)
                ),
                // "Then you may put an exiled creature card used to craft The Grim Captain onto the
                // battlefield under your control tapped and attacking."
                GatherCardsEffect(
                    source = CardSource.CraftedMaterials,
                    storeAs = "grimCaptainCrafted"
                ),
                SelectFromCollectionEffect(
                    from = "grimCaptainCrafted",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Creature,
                    showAllCards = true,
                    storeSelected = "grimCaptainReturn",
                    prompt = "Put an exiled creature card used to craft The Grim Captain onto the battlefield tapped and attacking",
                    selectedLabel = "Put onto the battlefield tapped and attacking"
                ),
                MoveCollectionEffect(
                    from = "grimCaptainReturn",
                    destination = CardDestination.ToZone(
                        Zone.BATTLEFIELD,
                        Player.You,
                        ZonePlacement.TappedAndAttacking
                    )
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Tiffany Turrill"
        imageUri = "https://cards.scryfall.io/normal/back/c/1/c13e8e3d-2a6b-4782-a3c9-71af7336a881.jpg?1782694399"
    }
}

val ThroneOfTheGrimCaptain: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = ThroneOfTheGrimCaptainFront,
    backFace = TheGrimCaptain
)
