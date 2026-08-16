package com.wingedsheep.sdk.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * [ManaCost.coloredSymbolCount] and the [ManaSymbol.colors] rule under it — the single "which mana
 * symbols are which colours" implementation shared by devotion (CR 700.5, summed across a player's
 * permanents) and by the per-object pip reads
 * ([com.wingedsheep.sdk.scripting.values.EntityNumericProperty.ColoredManaSymbolCount] /
 * [com.wingedsheep.sdk.scripting.predicates.CardPredicate.ColoredManaSymbolsAtLeast], Namor the
 * Sub-Mariner).
 *
 * The rules being pinned down:
 *  - CR 107.4e — a hybrid symbol *is* a colored mana symbol and is **all** of its component
 *    colours, and a monocolored hybrid ("twobrid") `{2/U}` is its colour.
 *  - CR 107.4f — a Phyrexian symbol is a colored mana symbol of its colour.
 *  - CR 107.4b/c — numerical, `{X}` and `{C}` symbols are not colored mana symbols.
 *  - CR 700.5 — "devotion to [c1] and [c2]" counts a symbol that is either colour, so a symbol
 *    which is *both* requested colours is still one symbol.
 *
 * **Out of scope here:** how a two-faced card presents a cost to be counted. `ManaCost` is handed
 * one cost and counts it; which cost a split card or MDFC exposes is a `CardDefinition`/zone
 * question (CR 709.3b — while on the stack only the cast half's characteristics exist; CR 709.4b —
 * an effect referring specifically to the symbols in a split card's mana cost sees the separate
 * symbols rather than the whole mana cost). Nothing in this file can assert that, so it doesn't
 * pretend to.
 */
class ManaCostColoredSymbolCountTest : StringSpec({

    fun count(cost: String, vararg colors: Color) =
        ManaCost.parse(cost).coloredSymbolCount(colors.toSet())

    // ----- plain colored pips -------------------------------------------------------------

    "a mono-blue cost counts its one blue pip" {
        count("{U}", Color.BLUE) shouldBe 1
    }
    "several blue pips are counted separately" {
        count("{1}{U}{U}", Color.BLUE) shouldBe 2
        count("{U}{U}{U}{U}", Color.BLUE) shouldBe 4
    }
    "off-colour pips are not counted" {
        count("{2}{R}{G}", Color.BLUE) shouldBe 0
        count("{U}{R}", Color.RED) shouldBe 1
    }

    // ----- costs with no colored pips at all ----------------------------------------------

    "a generic-only cost counts nothing" {
        count("{5}", Color.BLUE) shouldBe 0
    }
    "colorless {C} is not a colour and counts nothing" {
        count("{C}{C}", Color.BLUE) shouldBe 0
    }
    "{X} is generic (CR 107.4b) and counts nothing, whatever X was announced" {
        count("{X}", Color.BLUE) shouldBe 0
        count("{X}{X}{U}", Color.BLUE) shouldBe 1
        ManaCost.parse("{X}{U}").withXAs(7).coloredSymbolCount(setOf(Color.BLUE)) shouldBe 1
    }
    "a spell with no mana cost at all counts nothing" {
        ManaCost.ZERO.coloredSymbolCount(setOf(Color.BLUE)) shouldBe 0
        count("", Color.BLUE) shouldBe 0
    }

    // ----- hybrid (CR 107.4e) --------------------------------------------------------------

    "a hybrid {U/R} is a blue mana symbol" {
        count("{U/R}", Color.BLUE) shouldBe 1
    }
    "the same hybrid {U/R} is also a red mana symbol" {
        count("{U/R}", Color.RED) shouldBe 1
    }
    "a hybrid of two other colours is neither" {
        count("{R/G}", Color.BLUE) shouldBe 0
    }
    "hybrids mix with plain pips" {
        count("{U/R}{U}{R}", Color.BLUE) shouldBe 2
    }
    "a monocolored hybrid {2/U} is a blue mana symbol (CR 107.4e)" {
        count("{2/U}", Color.BLUE) shouldBe 1
        count("{2/U}{2/U}", Color.BLUE) shouldBe 2
        count("{2/B}", Color.BLUE) shouldBe 0
    }

    // ----- Phyrexian (CR 107.4f) -----------------------------------------------------------

    "a Phyrexian {U/P} is a blue mana symbol" {
        count("{U/P}", Color.BLUE) shouldBe 1
        count("{1}{U/P}{U}", Color.BLUE) shouldBe 2
    }
    "a Phyrexian pip of another colour is not blue" {
        count("{G/P}", Color.BLUE) shouldBe 0
    }

    // ----- multi-colour requests (CR 700.5's two-colour devotion) ---------------------------

    "asking for two colours counts a pip of either" {
        count("{U}{R}{G}", Color.BLUE, Color.RED) shouldBe 2
    }
    "a hybrid that is both requested colours is still one symbol" {
        count("{U/R}", Color.BLUE, Color.RED) shouldBe 1
    }
    "an empty colour set counts nothing" {
        ManaCost.parse("{U}{U}").coloredSymbolCount(emptySet()) shouldBe 0
    }

    // ----- the ManaSymbol.colors rule itself ------------------------------------------------

    "ManaSymbol.colors: a hybrid is all of its component colours (CR 107.4e)" {
        ManaSymbol.Hybrid(Color.BLUE, Color.RED).colors shouldBe setOf(Color.BLUE, Color.RED)
    }
    "ManaSymbol.colors: a Phyrexian pip is its colour (CR 107.4f)" {
        ManaSymbol.Phyrexian(Color.BLUE).colors shouldBe setOf(Color.BLUE)
    }
    "ManaSymbol.colors: a twobrid is its colour, not colourless" {
        ManaSymbol.MonocolorHybrid(2, Color.BLUE).colors shouldBe setOf(Color.BLUE)
    }
    "ManaSymbol.colors: generic, colorless and X are no colour" {
        ManaSymbol.Generic(3).colors shouldBe emptySet()
        ManaSymbol.Colorless.colors shouldBe emptySet()
        ManaSymbol.X.colors shouldBe emptySet()
    }

    // ----- ManaCost.colors still agrees with the shared rule (CR 202.2c/d) -------------------

    "ManaCost.colors reads the same symbol rule — hybrids and Phyrexians colour the object" {
        ManaCost.parse("{2}{U/R}").colors shouldBe setOf(Color.BLUE, Color.RED)
        ManaCost.parse("{U/P}{G}").colors shouldBe setOf(Color.BLUE, Color.GREEN)
        ManaCost.parse("{2/B}").colors shouldBe setOf(Color.BLACK)
        ManaCost.parse("{4}{C}{X}").colors shouldBe emptySet()
    }
})
