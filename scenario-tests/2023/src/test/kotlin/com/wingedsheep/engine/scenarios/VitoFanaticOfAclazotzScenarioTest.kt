package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vito, Fanatic of Aclazotz (LCI #243) — {2}{W}{B} Legendary Creature — Vampire Demon, 4/4, flying.
 *
 * "Whenever you sacrifice another permanent, you gain 2 life if this is the first time this ability
 * has resolved this turn. If it's the second time, each opponent loses 2 life. If it's the third
 * time, create a 4/3 white and black Vampire Demon creature token with flying."
 *
 * The "Victor template": one escalating trigger keyed to how many times *this ability* has resolved
 * this turn (Victor, Valgavoth's Seneschal). Each resolution bumps the source's per-turn resolution
 * counter (IncrementAbilityResolutionCountEffect) then runs exactly one tier via
 * Conditions.SourceAbilityResolvedNTimes(n). Driven here with a {0} "Sacrifice target permanent"
 * sorcery fired four times in one turn: the three tiers escalate, and the fourth resolution
 * matches no tier and does nothing.
 */
class VitoFanaticOfAclazotzScenarioTest : ScenarioTestBase() {

    // {0} sorcery: sacrifice a target permanent you control — a clean single-sacrifice outlet.
    private val sacrificePermanent = card("Sacrifice Permanent") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Sacrifice target permanent you control."
        spell {
            val t = target("target permanent you control", Targets.Permanent)
            effect = Effects.SacrificeTarget(t)
        }
    }

    // 1/1 vanilla fodder to sacrifice.
    private val fodder = card("Blood Fodder") {
        manaCost = "{0}"
        typeLine = "Creature — Vampire"
        power = 1
        toughness = 1
    }

    init {
        // Vito is auto-discovered from the LCI cards package; only the test-local cards
        // need explicit registration.
        cardRegistry.register(sacrificePermanent)
        cardRegistry.register(fodder)

        context("Vito, Fanatic of Aclazotz") {

            test("escalates across three sacrifices this turn: gain 2 life, opponent loses 2 life, then a token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withCardOnBattlefield(1, "Vito, Fanatic of Aclazotz", summoningSickness = false)
                    .withCardOnBattlefield(1, "Blood Fodder", summoningSickness = false)
                    .withCardOnBattlefield(1, "Blood Fodder", summoningSickness = false)
                    .withCardOnBattlefield(1, "Blood Fodder", summoningSickness = false)
                    .withCardOnBattlefield(1, "Blood Fodder", summoningSickness = false)
                    .withCardInHand(1, "Sacrifice Permanent")
                    .withCardInHand(1, "Sacrifice Permanent")
                    .withCardInHand(1, "Sacrifice Permanent")
                    .withCardInHand(1, "Sacrifice Permanent")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("baseline life totals") {
                    game.getLifeTotal(1) shouldBe 20
                    game.getLifeTotal(2) shouldBe 20
                }

                // --- 1st resolution: you gain 2 life. ---
                val fodder1 = game.findPermanents("Blood Fodder")[0]
                game.castSpell(1, "Sacrifice Permanent", fodder1).error shouldBe null
                game.resolveStack()
                withClue("first Vito resolution: you gain 2 life; opponent unchanged") {
                    game.getLifeTotal(1) shouldBe 22
                    game.getLifeTotal(2) shouldBe 20
                }

                // --- 2nd resolution: each opponent loses 2 life. ---
                val fodder2 = game.findPermanents("Blood Fodder")[0]
                game.castSpell(1, "Sacrifice Permanent", fodder2).error shouldBe null
                game.resolveStack()
                withClue("second Vito resolution: each opponent loses 2 life; your life unchanged") {
                    game.getLifeTotal(1) shouldBe 22
                    game.getLifeTotal(2) shouldBe 18
                }

                // --- 3rd resolution: create a 4/3 W/B Vampire Demon flying token. ---
                withClue("no token before the third resolution") {
                    game.isOnBattlefield("Vampire Demon Token") shouldBe false
                }
                val fodder3 = game.findPermanents("Blood Fodder")[0]
                game.castSpell(1, "Sacrifice Permanent", fodder3).error shouldBe null
                game.resolveStack()
                withClue("third Vito resolution: a Vampire Demon token is created; life totals unchanged") {
                    game.getLifeTotal(1) shouldBe 22
                    game.getLifeTotal(2) shouldBe 18
                    game.isOnBattlefield("Vampire Demon Token") shouldBe true
                }

                // --- 4th resolution: no tier matches (== 4 is past the third), nothing happens. ---
                val fodder4 = game.findPermanents("Blood Fodder")[0]
                game.castSpell(1, "Sacrifice Permanent", fodder4).error shouldBe null
                game.resolveStack()
                withClue("fourth Vito resolution: no life change and no second token") {
                    game.getLifeTotal(1) shouldBe 22
                    game.getLifeTotal(2) shouldBe 18
                    game.findPermanents("Vampire Demon Token").size shouldBe 1
                }
            }
        }
    }
}
