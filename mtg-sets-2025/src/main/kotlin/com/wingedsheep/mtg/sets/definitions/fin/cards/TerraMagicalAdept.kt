package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ReturnFace
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Terra, Magical Adept // Esper Terra (Final Fantasy #245)
 * {1}{R}{G} — Legendary Creature — Human Wizard Warrior 4/2
 * //  — Legendary Enchantment Creature — Saga Wizard 6/6 (Flying)
 *
 * Front — Terra, Magical Adept:
 *   When Terra enters, mill five cards. Put up to one enchantment card milled this way into your hand.
 *   Trance — {4}{R}{G}, {T}: Exile Terra, then return it to the battlefield transformed under its
 *   owner's control. Activate only as a sorcery.
 *
 * Back — Esper Terra (a Summon-style Saga creature):
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I, II, III — Create a token that's a copy of target nonlegendary enchantment you control. It gains
 *   haste. If it's a Saga, put up to three lore counters on it. Sacrifice it at the beginning of your
 *   next end step.
 *   IV — Add {W}{W}, {U}{U}, {B}{B}, {R}{R}, and {G}{G}. Exile Esper Terra, then return it to the
 *   battlefield (front face up).
 *
 * The chapter I–III copy is composed from atoms: `CreateTokenCopyOfTarget` (haste via addedKeywords,
 * sacrifice at the controller's next end step via sacrificeAtStep) publishes the token into
 * `CREATED_TOKENS`; a `ConditionalEffect` gated on that collection containing a Saga then runs
 * `AddCountersUpTo(LORE, 3, …)` so the controller may advance the copied Saga's chapters. Chapter IV
 * exile-returns Esper Terra front face up before the CR 714.4 final-chapter sacrifice applies (the
 * chapter is on the stack when lore reaches four, and on resolution the permanent is no longer a Saga),
 * mirroring the Dominant eikon backs — so no sacrifice-opt-out flag is needed.
 *
 * A token copy of a Saga made by chapter I–III enters as a Saga with CR 714.2b's on-enter lore counter
 * (its chapter I triggers) — the token-copy executors route through the shared
 * `ZoneMovementUtils.applySagaEntryIfNeeded` Saga-entry hook, so the "put up to three lore counters"
 * clause stacks on top of that starting counter.
 */

// Shared chapter I–III effect, parameterized on the chosen target enchantment.
private fun copyChapterEffect(chosen: EffectTarget): Effect = Effects.Composite(
    Effects.CreateTokenCopyOfTarget(
        target = chosen,
        addedKeywords = setOf(Keyword.HASTE),
        // "Sacrifice it at the beginning of your next end step."
        sacrificeAtStep = Step.END,
        sacrificeOnlyOnControllersTurn = true,
    ),
    // "If it's a Saga, put up to three lore counters on it." The created token is in CREATED_TOKENS;
    // gate on it being a Saga, then let the controller choose 0..3 lore counters (advancing its
    // chapters). Copying a non-Saga enchantment offers no lore prompt.
    ConditionalEffect(
        condition = Conditions.CollectionContainsMatch(
            CREATED_TOKENS,
            GameObjectFilter.Enchantment.withSubtype(Subtype.SAGA),
        ),
        effect = Effects.AddCountersUpTo(Counters.LORE, 3, EffectTarget.PipelineTarget(CREATED_TOKENS, 0)),
    ),
)

private val EsperTerra = card("Esper Terra") {
    manaCost = ""
    colorIdentity = "WUBRG"
    typeLine = "Legendary Enchantment Creature — Saga Wizard"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I, II, III — Create a token that's a copy of target nonlegendary enchantment you control. " +
        "It gains haste. If it's a Saga, put up to three lore counters on it. Sacrifice it at the " +
        "beginning of your next end step.\n" +
        "IV — Add {W}{W}, {U}{U}, {B}{B}, {R}{R}, and {G}{G}. Exile Esper Terra, then return it to " +
        "the battlefield (front face up)."
    power = 6
    toughness = 6
    keywords(Keyword.FLYING)

    // I, II, III — copy a nonlegendary enchantment you control.
    for (chapter in 1..3) {
        sagaChapter(chapter) {
            val enchantment = target(
                "enchantment",
                TargetObject(filter = TargetFilter.Enchantment.youControl().nonlegendary()),
            )
            effect = copyChapterEffect(enchantment)
        }
    }

    // IV — Add {W}{W}{U}{U}{B}{B}{R}{R}{G}{G}, then exile Esper Terra and return it front face up.
    sagaChapter(4) {
        effect = Effects.Composite(
            Effects.AddMana(Color.WHITE, 2),
            Effects.AddMana(Color.BLUE, 2),
            Effects.AddMana(Color.BLACK, 2),
            Effects.AddMana(Color.RED, 2),
            Effects.AddMana(Color.GREEN, 2),
            Effects.ExileAndReturnTransformed(EffectTarget.Self, ReturnFace.FRONT),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "245"
        artist = "Clare Wong"
        imageUri = "https://cards.scryfall.io/normal/back/f/b/fbd447aa-588d-4c4d-925e-a7d3bdf6a65c.jpg?1782686407"
        ruling("2025-06-06", "Esper Terra's fourth chapter ability isn't a mana ability. It uses the stack and can be responded to.")
        ruling("2025-06-06", "The token copies exactly what was printed on the original enchantment. It doesn't copy counters, Auras/Equipment, or non-copy effects. If the copied enchantment has {X} in its mana cost, X is 0.")
    }
}

private val TerraMagicalAdeptFront = card("Terra, Magical Adept") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Human Wizard Warrior"
    oracleText = "When Terra enters, mill five cards. Put up to one enchantment card milled this way " +
        "into your hand.\n" +
        "Trance — {4}{R}{G}, {T}: Exile Terra, then return it to the battlefield transformed under " +
        "its owner's control. Activate only as a sorcery."
    power = 4
    toughness = 2

    // When Terra enters, mill five cards. Put up to one enchantment card milled this way into hand.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.mill(5),
            SelectFromCollectionEffect(
                from = "milled",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter.Enchantment,
                storeSelected = "selected",
                showAllCards = true,
                prompt = "You may put an enchantment card milled this way into your hand",
                selectedLabel = "Put in hand",
                remainderLabel = "Leave in graveyard",
            ),
            MoveCollectionEffect(
                from = "selected",
                destination = CardDestination.ToZone(Zone.HAND),
            ),
        )
    }

    // Trance — {4}{R}{G}, {T}: Exile Terra, then return it transformed. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{R}{G}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "245"
        artist = "Clare Wong"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbd447aa-588d-4c4d-925e-a7d3bdf6a65c.jpg?1782686407"
    }
}

val TerraMagicalAdept: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = TerraMagicalAdeptFront,
    backFace = EsperTerra,
)
