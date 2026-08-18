package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.HumOfTheRadix
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Hum of the Radix (MRD #122) — "Each artifact spell costs {1} more to cast for each artifact its
 * controller controls."
 *
 * Two things separate this from every tax already in the corpus, and a wrong implementation passes
 * a naive test on both. The tax is *dynamic*, so it has to be recomputed off the board rather than
 * read off the card; and it is charged against **the caster's** artifacts, not the enchantment
 * controller's — a distinction that is invisible whenever one player happens to control all the
 * artifacts, which is exactly the board a single-player test builds. So the load-bearing case here
 * is the asymmetric one: the same artifact spell priced for both players at once, each paying for
 * their own board.
 *
 * The remaining cases pin the edges the tax could silently overreach into: no artifacts is no tax
 * (not a floor of one), and a nonartifact spell is untouched however many artifacts are out.
 */
class HumOfTheRadixScenarioTest : FunSpec({

    fun registry(): CardRegistry = CardRegistry().apply {
        register(TestCards.all)
        register(HumOfTheRadix)
    }

    /** Player 1's precombat main with the Hum already resolved under player 1's control. */
    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + HumOfTheRadix)
        d.initMirrorMatch(deck = Deck.of("Forest" to 30), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(d.player1, "Hum of the Radix")
        return d
    }

    /** The generic component of what [caster] would pay for the {2} artifact creature. */
    fun GameTestDriver.artifactSpellGenericFor(caster: EntityId): Int {
        val registry = registry()
        return CostCalculator(registry)
            .calculateEffectiveCost(state, registry.requireCard("Artifact Creature"), caster)
            .genericAmount
    }

    test("an artifact spell is taxed {1} for each artifact its caster controls") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")

        withClue("{2} printed plus {1} per artifact controlled — two artifacts out, so {4}") {
            d.artifactSpellGenericFor(d.player1) shouldBe 4
        }
    }

    test("with no artifacts on the board the tax is zero, not a minimum of one") {
        val d = driver()

        withClue("the Hum itself is an enchantment, so it never taxes its own controller") {
            d.artifactSpellGenericFor(d.player1) shouldBe 2
        }
    }

    test("each player pays for their own artifacts, not the Hum controller's") {
        // The whole point of the card: "its controller" is the *casting* player. A tax evaluated
        // against the enchantment's controller would price both columns at 3.
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")
        d.putPermanentOnBattlefield(opponent, "Artifact Creature")
        d.putPermanentOnBattlefield(opponent, "Artifact Creature")
        d.putPermanentOnBattlefield(opponent, "Artifact Creature")

        withClue("player 1 controls one artifact") {
            d.artifactSpellGenericFor(d.player1) shouldBe 3
        }
        withClue("the opponent controls three, and is taxed for those rather than for player 1's") {
            d.artifactSpellGenericFor(opponent) shouldBe 5
        }
    }

    test("nonartifact spells are untouched however many artifacts are on the battlefield") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")
        d.putPermanentOnBattlefield(d.player1, "Artifact Creature")

        val registry = registry()
        val cost = CostCalculator(registry)
            .calculateEffectiveCost(d.state, registry.requireCard("Black Creature"), d.player1)

        withClue("{1}{B} stays {1}{B} — the filter is artifact spells, not all spells") {
            cost.genericAmount shouldBe 1
            cost.cmc shouldBe 2
        }
    }
})
